package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.functionality.recommendationSystem.MakeMedicalRecommendation;
import com.aitherapist.aitherapist.interactionWithGigaApi.inputParser.ParseUserPrompt;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.model.ClientRegistrationState;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class WriteDailyData implements ICommand {
    private final PatientServiceImpl patientService;
    private final ParseUserPrompt parseUserPrompt;
    private final MakeMedicalRecommendation makeMedicalRecommendation;
    @Autowired
    public WriteDailyData(PatientServiceImpl patientService, ParseUserPrompt parseUserPrompt, MakeMedicalRecommendation makeMedicalRecommendation) {
        this.patientService = patientService;
        this.parseUserPrompt = parseUserPrompt;
        this.makeMedicalRecommendation = makeMedicalRecommendation;
    }

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


    private SendMessage handleQuestionnaire(Update update, Long userId, RegistrationContext registrationContext) throws TelegramApiException, JsonProcessingException {
        Long chatId = TelegramIdUtils.getChatId(update);
        String text = "";
        if (update.hasMessage()) {
            //registrationContext.resetClientRegistrationState(userId);
            text = update.getMessage().getText();
        }
        ClientRegistrationState state = registrationContext.getClientRegistrationState(chatId);
        switch (state.getCurrentStep()) {
            case 1 -> {
                state.setCurrentStep(2);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.MEDICAL_DATA_INPUT.getMessage())
                        .build();
            }
            case 2 -> {
                state.getBase().append(text);
                String response = parseUserPrompt.dailyQuestionnaireParser(state.getBase().toString());
                DailyHealthData d = mapper.readValue(response, DailyHealthData.class);

                Patient currentPatient = patientService.getPatientWithData(userId);
                patientService.addDailyHealthDataToPatient(userId, d);
                currentPatient = patientService.getPatientWithData(userId);
                String response4 =
                        makeMedicalRecommendation.giveMedicalRecommendationWithScoreBeta((ClinicPatient) currentPatient);
                registrationContext.setStatus(userId, Status.NONE);
                registrationContext.clearClientRegistrationState(userId);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(response4 != null ? response4 : "Рекомендации не сгенерированы")
                        .parseMode("HTML")
                        .replyMarkup(InlineKeyboardFactory.createPatientDefaultKeyboard(patientService.findById(userId)))
                        .build();
            }
            default -> {
                registrationContext.clearClientRegistrationState(userId);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Неизвестный шаг регистрации")
                        .build();
            }
        }
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        registrationContext.setStatus(userId, Status.WRITE_DAILY_DATA);
        try {
            return handleQuestionnaire(update, userId, registrationContext);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}