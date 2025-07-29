package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.domain.model.entities.UserActivityLog;
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
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class WriteDailyData implements ICommand {
    private final PatientServiceImpl patientService;
    private final UserServiceImpl userService;
    private final ParseUserPrompt parseUserPrompt;
    private final MakeMedicalRecommendation makeMedicalRecommendation;

    @Autowired
    public WriteDailyData(PatientServiceImpl patientService, UserServiceImpl userService,
                          ParseUserPrompt parseUserPrompt, MakeMedicalRecommendation makeMedicalRecommendation) {
        this.patientService = patientService;
        this.userService = userService;
        this.parseUserPrompt = parseUserPrompt;
        this.makeMedicalRecommendation = makeMedicalRecommendation;
    }

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Transactional
    protected void saveActivityTime(Long userId) {
        UserActivityLog activityLog = UserActivityLog.builder()
                .user(patientService.findById(userId))
                .actionTime(LocalDateTime.now())
                .actionType("ENTER_WRITE_DAILY_DATA")
                .build();
        userService.saveUserActivityLog(userId, activityLog);

        String optimalTimeRecommendation = getOptimalNotificationTimeRecommendation(userId);
    }


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

                Patient currentPatient = patientService.getPatientWithData(userId);
                patientService.addDailyHealthDataToPatient(userId, d);
                currentPatient = patientService.getPatientWithData(userId);
                String response4 = makeMedicalRecommendation.giveMedicalRecommendationWithScore(currentPatient);


                String fullResponse =
                        (response4 != null ? response4 : "Рекомендации не сгенерированы");

                registrationContext.setStatus(userId, Status.NONE);
                registrationContext.clearClientRegistrationState(userId);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(fullResponse)
                        .parseMode("HTML")
                        .replyMarkup(InlineKeyboardFactory.createPatientDefaultKeyboard(patientService.findById(userId)))
                        .build();
            }
            default -> {
                registrationContext.clearClientRegistrationState(userId);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Неизвестный шаг регистрации\n\n" )
                        .build();
            }
        }
    }

    @Transactional
    protected String getOptimalNotificationTimeRecommendation(Long userId) {
        List<UserActivityLog> logs = userService.getUser(userId).getActivityLogs();

        if (logs.isEmpty()) {
            return "⏱ Пока недостаточно данных для рекомендации оптимального времени уведомлений.";
        }

        long totalSeconds = logs.stream()
                .mapToLong(log -> log.getActionTime().toLocalTime().toSecondOfDay())
                .sum();

        int avgSeconds = (int) (totalSeconds / logs.size());
        LocalTime optimalTime = LocalTime.ofSecondOfDay(avgSeconds);

        return String.format("⏱ На основе вашей активности (%d записей), рекомендуемое время для уведомлений: %02d:%02d",
                logs.size(), optimalTime.getHour(), optimalTime.getMinute());
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor)
            throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        registrationContext.setStatus(userId, Status.WRITE_DAILY_DATA);

        //saveActivityTime(userId);

        try {
            return handleQuestionnaire(update, userId, registrationContext);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}