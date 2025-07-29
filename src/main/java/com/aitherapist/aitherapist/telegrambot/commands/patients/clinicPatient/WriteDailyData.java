package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.functionality.recommendationSystem.MakeMedicalRecommendation;
import com.aitherapist.aitherapist.interactionWithGigaApi.inputParser.ParseUserPrompt;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
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
    private final UserServiceImpl userService;
    private final ParseUserPrompt parseUserPrompt;
    private final MakeMedicalRecommendation makeMedicalRecommendation;
    private final RegistrationContext registrationContext;

    @Autowired
    public WriteDailyData(PatientServiceImpl patientService, UserServiceImpl userService,
                          ParseUserPrompt parseUserPrompt, MakeMedicalRecommendation makeMedicalRecommendation, RegistrationContext registrationContext) {
        this.patientService = patientService;
        this.userService = userService;
        this.parseUserPrompt = parseUserPrompt;
        this.makeMedicalRecommendation = makeMedicalRecommendation;
        this.registrationContext = registrationContext;
    }

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    protected SendMessage handleQuestionnaire(Update update, Long userId, RegistrationContext registrationContext)
            throws TelegramApiException, JsonProcessingException {

        Long chatId = TelegramIdUtils.getChatId(update);
        String text = update.hasMessage() ? update.getMessage().getText() : "";

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

                if (!areHealthParametersNormal(d)) {
                    state.setCurrentStep(3);
                    return SendMessage.builder()
                            .chatId(chatId.toString())
                            .text(" Как вы себя чувствуете сегодня?")
                            .build();
                } else {
                    return processFinalResponse(chatId, userId, registrationContext, d, null);
                }
            }
            case 3 -> {
                String wellbeing = text;
                String response = parseUserPrompt.dailyQuestionnaireParser(state.getBase().toString());
                DailyHealthData d = mapper.readValue(response, DailyHealthData.class);
                d.setFeels(wellbeing);

                registrationContext.clearClientRegistrationState(userId);
                registrationContext.setStatus(userId, Status.WRITE_DAILY_DATA);

                return processFinalResponse(chatId, userId, registrationContext, d, wellbeing);
            }
            default -> {
                registrationContext.clearClientRegistrationState(userId);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Неизвестный шаг регистрации\n\n")
                        .build();
            }
        }
    }

    private SendMessage processFinalResponse(Long chatId, Long userId, RegistrationContext context,
                                             DailyHealthData data, String wellbeing) throws TelegramApiException {
        Patient patient = patientService.getPatientWithData(userId);

        patientService.addDailyHealthDataToPatient(userId, data);

        String recommendations = makeMedicalRecommendation.giveMedicalRecommendationWithScore(patient);
        registrationContext.removeClientRegistrationStates(userId);
        context.setStatus(userId, Status.NONE);
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(recommendations != null ? recommendations : "Рекомендации не сгенерированы")
                .parseMode("HTML")
                .replyMarkup(InlineKeyboardFactory.createPatientDefaultKeyboard(patientService.findById(userId)))
                .build();
    }

    private boolean areHealthParametersNormal(DailyHealthData data) {


        // Проверка температуры (норма: 36.0-37.2°C)
        if (data.getTemperature() == null || data.getTemperature() < 36.0 || data.getTemperature() > 37.2) {
            return false;
        }

        if (data.getHoursOfSleepToday() == null || data.getHoursOfSleepToday() < 6) {
            return false;
        }

        // Проверка пульса (норма: 60-100 уд/мин)
        if (data.getPulse() == null || data.getPulse() < 60 || data.getPulse() > 100) {
            return false;
        }

        if (data.getPressure() != null) {
            try {
                String[] parts = data.getPressure().split("/");
                int systolic = Integer.parseInt(parts[0]);
                int diastolic = Integer.parseInt(parts[1]);

                if (systolic < 100 || systolic > 140 || diastolic < 60 || diastolic > 90) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor)
            throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        registrationContext.setStatus(userId, Status.WRITE_DAILY_DATA);

        try {
            return handleQuestionnaire(update, userId, registrationContext);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}