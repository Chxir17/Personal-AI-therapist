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
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WriteDailyData implements ICommand {
    private final PatientServiceImpl patientService;
    private final UserServiceImpl userService;
    private final ParseUserPrompt parseUserPrompt;
    private final MakeMedicalRecommendation makeMedicalRecommendation;
    private final RegistrationContext registrationContext;
    private final Map<Long, String> mapResponse = new ConcurrentHashMap<>();
    @Autowired
    public WriteDailyData(PatientServiceImpl patientService, UserServiceImpl userService,
                          ParseUserPrompt parseUserPrompt, MakeMedicalRecommendation makeMedicalRecommendation, RegistrationContext registrationContext, TelegramMessageSender messageSender) {
        this.patientService = patientService;
        this.userService = userService;
        this.parseUserPrompt = parseUserPrompt;
        this.makeMedicalRecommendation = makeMedicalRecommendation;
        this.registrationContext = registrationContext;
        this.messageSender = messageSender;
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
                DailyHealthData d;
                try {
                    String response = parseUserPrompt.dailyQuestionnaireParser(state.getBase().toString());
                    mapResponse.clear();
                    mapResponse.put(chatId, response);
                    d = mapper.readValue(response, DailyHealthData.class);
                    if (d.getTemperature() == null && d.getHoursOfSleepToday() == null  && d.getPressure() == null && d.getBloodOxygenLevel() == null) {
                        return SendMessage.builder()
                                .chatId(chatId.toString())
                                .text("Попробуйте еще раз!")
                                .build();
                    }
                }
                catch (JsonProcessingException e) {
                      return SendMessage.builder()
                            .chatId(chatId.toString())
                            .text("Попробуйте еще раз!")
                            .build();
                }


                if (!areHealthParametersNormal(d)) {
                    state.setCurrentStep(3);
                    validateAndCleanHealthParameters(d);
                    return SendMessage.builder()
                            .chatId(chatId.toString())
                            .text(" Как вы себя чувствуете сегодня?")
                            .build();

                } else {
                    validateAndCleanHealthParameters(d);
                    return processFinalResponse(chatId, userId, registrationContext, d, null);
                }
            }
            case 3 -> {
                String wellbeing = text;

                DailyHealthData d = mapper.readValue(mapResponse.get(userId), DailyHealthData.class);
                validateAndCleanHealthParameters(d);
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
                .text(recommendations != null ? recommendations.replace("\\n", "\n").replace("/n", "\n") : "Рекомендации не сгенерированы")
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
        registrationContext.removeClientRegistrationStatesCheck(userId);
        try {
            messageSender.sendMessageAndSetToList(handleQuestionnaire(update, userId, registrationContext), registrationContext, userId);
            return null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateAndCleanHealthParameters(DailyHealthData data) {
        if (data.getTemperature() == null || data.getTemperature() < 13.0 || data.getTemperature() > 48.0) {
            data.setTemperature(null);
        }
        if (data.getHoursOfSleepToday() == null || data.getHoursOfSleepToday() < 0 || data.getHoursOfSleepToday() > 50) {
            data.setHoursOfSleepToday(null);
        }
        if (data.getPulse() == null || data.getPulse() < 20 || data.getPulse() > 350) {
            data.setPulse(null);
        }
        if (data.getPressure() != null) {
            try {
                String[] parts = data.getPressure().split("/");
                int systolic = Integer.parseInt(parts[0]);
                int diastolic = Integer.parseInt(parts[1]);

                if (systolic < 30 || systolic > 370 || diastolic < 40 || diastolic > 380) {
                    data.setPressure(null);
                }
            } catch (Exception e) {
                data.setPressure(null);
            }
        }
    }
}