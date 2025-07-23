package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.functionality.recommendationSystem.MakeMedicalRecommendation;
import com.aitherapist.aitherapist.interactionWithGigaApi.inputParser.ParseUserPrompt;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.ClientRegistrationState;
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
    private UserServiceImpl userService;
    private PatientServiceImpl patientService;
    @Autowired
    public WriteDailyData(UserServiceImpl userService, PatientServiceImpl patientService) {
        this.userService = userService;
        this.patientService = patientService;
    }

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


    private SendMessage handleQuestionnaire(Update update, Long userId, RegistrationContext registrationContext) throws TelegramApiException, JsonProcessingException {
        Long chatId = TelegramIdUtils.getChatId(update);
        ClientRegistrationState state = registrationContext.getClientRegistrationState(chatId);
        String text = "";
        if (update.hasMessage()) {
            text = update.getMessage().getText();
        }
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
                String response = ParseUserPrompt.dailyQuestionnaireParser(state.getBase().toString());
                System.out.println("response - " + response);
                DailyHealthData d = mapper.readValue(response, DailyHealthData.class);
                System.out.println("daily healthdata - " + d.toString());

                Patient currentPatient = patientService.getPatientWithData(userId);
                patientService.addDailyHealthDataToPatient(userId, d);
                System.out.println("patient created " + currentPatient.toString());
                currentPatient = patientService.getPatientWithData(userId);
                String response4 =
                        MakeMedicalRecommendation.giveMedicalRecommendationWithScoreBeta((ClinicPatient) currentPatient);
                System.out.println("response4 " + response4);
                registrationContext.setStatus(userId, Status.NONE);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(response4 != null ? response4 : "Рекомендации не сгенерированы")
                        .parseMode("HTML")
                        .replyMarkup(InlineKeyboardFactory.createPatientDefaultKeyboard())
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