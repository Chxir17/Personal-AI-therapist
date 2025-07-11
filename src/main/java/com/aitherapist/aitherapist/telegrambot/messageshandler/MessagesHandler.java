package com.aitherapist.aitherapist.telegrambot.messageshandler;

import com.aitherapist.aitherapist.services.registration.UserRegistrationService;
import com.aitherapist.aitherapist.domain.model.entities.HealthData;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.interactionWithGigaApi.MakeMedicalRecommendation;
import com.aitherapist.aitherapist.interactionWithGigaApi.ParseUserPrompt;
import com.aitherapist.aitherapist.domain.model.entities.MedicalAnalysisResult;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.springframework.context.annotation.Lazy;

@Getter
@Setter
@Component
@RequiredArgsConstructor
@Slf4j
public class MessagesHandler implements IHandler {
    private final RestTemplate restTemplate = new RestTemplate();
    private MedicalAnalysisResult medicalAnalysisResult;
    private final RegistrationContext registrationContext;
    private final DataController dataController;
    private final UserRegistrationService userRegistrationService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    @Lazy
    private IMessageSender messageSender;
    private final ParseUserPrompt parseUserPrompt = new ParseUserPrompt();
    private final MakeMedicalRecommendation makeMedicalRecommendation = new MakeMedicalRecommendation();

    @Override
    public void handle(Update update) throws TelegramApiException, JsonProcessingException {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        int userId = getUserId(update);

        if (registrationContext.isRegistrationInProgress(chatId)) {
            handleRegistration(chatId, userId, messageText);
        } else {
            handleHealthData(chatId, userId, messageText, update);
        }
    }

    private int getUserId(Update update) {
        return Math.toIntExact(update.getMessage().getFrom().getId());
    }

    private void handleRegistration(long chatId, int userId, String messageText) throws TelegramApiException {
        try {
            User user = parseUserRegistrationData(messageText);
            registerUser(userId, user);
            sendSuccessMessage(chatId, Answers.REGISTRATION_SUCCESSFUL.getMessage());
            registrationContext.deleteRegistration(chatId);
        } catch (Exception e) {
            logRegistrationError(chatId, e);
        }
    }

    private User parseUserRegistrationData(String messageText) throws JsonProcessingException, InterruptedException {
        String rawJsonResponse = parseUserPrompt.initPromptParser(messageText);
        String cleanJson = cleanJsonResponse(rawJsonResponse);
        return mapper.readValue(cleanJson, User.class);
    }

    private void registerUser(int userId, User user) {
        userRegistrationService.registerUser(userId, user);
    }

    private void handleHealthData(long chatId, int userId, String messageText, Update update)
            throws TelegramApiException, JsonProcessingException {
        sendInitialResponse(chatId);
        HealthData healthData = parseHealthData(messageText);
        saveHealthData(userId, healthData);
        String recommendation = generateMedicalRecommendation(update);
        sendRecommendation(chatId, recommendation);
    }

    private HealthData parseHealthData(String messageText) throws JsonProcessingException {
        String rawJsonResponse = ParseUserPrompt.dailyQuestionnaireParser(messageText);

        String cleanJson = cleanJsonResponse(rawJsonResponse);
        System.out.println(cleanJson);
        return mapper.readValue(cleanJson, HealthData.class);
    }

    private String cleanJsonResponse(String jsonResponse) {
        return jsonResponse.replaceAll("```json|```", "").trim();
    }

    private void saveHealthData(int userId, HealthData healthData) {
        userRegistrationService.putHealthDataInUser(userId, healthData);
    }

    private String generateMedicalRecommendation(Update update) {
        User user = userRegistrationService.getUserByUserId(getUserId(update));
        return MakeMedicalRecommendation.giveMedicalRecommendation(user);
    }

    private void sendInitialResponse(long chatId) throws TelegramApiException {
        messageSender.sendMessage(chatId, Answers.GIVE_ANSWER.getMessage());
    }

    private void sendRecommendation(long chatId, String recommendation) throws TelegramApiException {
        messageSender.sendMessage(chatId, recommendation);
    }

    private void sendSuccessMessage(long chatId, String message) throws TelegramApiException {
        messageSender.sendMessage(chatId, message);
    }

    private void logRegistrationError(long chatId, Exception e) {
        log.error("Error during registration processing", e);
        try {
            messageSender.sendMessage(chatId, Answers.REGISTRATION_ERROR.getMessage());
        } catch (TelegramApiException ex) {
            log.error("Failed to send error message", ex);
        }
    }
}