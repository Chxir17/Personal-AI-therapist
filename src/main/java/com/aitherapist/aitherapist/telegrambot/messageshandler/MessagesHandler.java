package com.aitherapist.aitherapist.telegrambot.messageshandler;

import com.aitherapist.aitherapist.db.dao.DataController;
import com.aitherapist.aitherapist.db.dao.logic.UserRegistrationService;
import com.aitherapist.aitherapist.db.entities.HealthData;
import com.aitherapist.aitherapist.db.entities.User;
import com.aitherapist.aitherapist.interactionWithGigaApi.MakeMedicalRecommendation;
import com.aitherapist.aitherapist.interactionWithGigaApi.ParseUserPrompt;
import com.aitherapist.aitherapist.telegrambot.commands.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.dto.MedicalAnalysisResult;
import com.aitherapist.aitherapist.telegrambot.utils.Answers;
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
/**
 * FIXME: maybe add apache kafka?
 * MessagesHandler - message handler.
 * check is this medical data.
 * parse data, put to database and send to ai assistent
 */
@Getter
@Setter
@Component
@RequiredArgsConstructor
@Slf4j
public class MessagesHandler implements IHandler {
    private final RestTemplate restTemplate = new RestTemplate();
    private MedicalAnalysisResult medicalAnalysisResult;
    private final RegistrationContext registrationContext;

    @Autowired
    private final DataController dataController;
    @Autowired
    private final UserRegistrationService userRegistrationService;

    private IMessageSender messageSender;
    private ParseUserPrompt parseUserPrompt = new ParseUserPrompt();
    private MakeMedicalRecommendation makeMedicalRecommendation = new MakeMedicalRecommendation();
    @Autowired
    public void setMessageSender(@Lazy IMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    /**
     * FIXME: add check is medical information.
     * canHandle - check is this medical data.
     * @param messageText
     * @return
     */
    @Override
    public boolean canHandle(String messageText) {
        return true;
    }

    /**
     * FIXME: to implement
     * Put information from Update(json) to database.
     * @param update
     * @return
     */
    @Override
    public void handle(Update update) throws TelegramApiException, JsonProcessingException {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        int userId = Math.toIntExact(update.getMessage().getFrom().getId());
        if (registrationContext.isRegistrationInProgress(chatId)) {
            try {
                System.out.println("Giga response raw:");
                System.out.println(messageText);
                String rawJsonResponse = parseUserPrompt.initPromptParser(messageText);
                String cleanJson = rawJsonResponse.replaceAll("```json|```", "").trim();
                ObjectMapper mapper = new ObjectMapper();
                User user = mapper.readValue(cleanJson, User.class);
                userRegistrationService.registerUser(
                        userId,
                        user
                );
                messageSender.sendMessage(chatId, Answers.REGISTRATION_SUCCESSFUL.getMessage());
            } catch (Exception e) {
                log.error("Error during registration processing", e);
                try {
                    messageSender.sendMessage(chatId, Answers.REGISTRATION_ERROR.getMessage());
                } catch (TelegramApiException ex) {
                    log.error("Failed to send error message", ex);
                }
            }
        } else {
            messageSender.sendMessage(chatId, Answers.GIVE_ANSWER.getMessage());
            String cleanJson = ParseUserPrompt.dailyQuestionnaireParser(messageText).replaceAll("```json|```", "").trim();
            ObjectMapper mapper = new ObjectMapper();
            HealthData healthData = mapper.readValue(cleanJson, HealthData.class);
            userRegistrationService.putHealthDataInUser(userId, healthData);
            String answer = MakeMedicalRecommendation.giveMedicalRecommendation(userRegistrationService.getUserByUserId(Math.toIntExact(update.getMessage().getFrom().getId())));
            messageSender.sendMessage(chatId, answer);
        }
    }
}
