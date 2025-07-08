package com.aitherapist.aitherapist.telegrambot.messageshandler;

import com.aitherapist.aitherapist.db.dao.DataController;
import com.aitherapist.aitherapist.JsonUserParser;
import com.aitherapist.aitherapist.db.entities.User;
import com.aitherapist.aitherapist.interactionWithGigaApi.MakeMedicalRecomendation;
import com.aitherapist.aitherapist.interactionWithGigaApi.ParseUserPrompt;
import com.aitherapist.aitherapist.telegrambot.commands.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.dto.MedicalAnalysisResult;
import com.aitherapist.aitherapist.telegrambot.utils.Answers;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
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
    private String pythonServiceUrl;
    private MedicalAnalysisResult medicalAnalysisResult;
    private final RegistrationContext registrationContext;
    private final DataController dataController;
    private IMessageSender messageSender;
    private ParseUserPrompt parseUserPrompt = new ParseUserPrompt();
    private MakeMedicalRecomendation makeMedicalRecomendation = new MakeMedicalRecomendation();
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
    public void handle(Update update) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        if (registrationContext.isRegistrationInProgress(chatId)) {
            try {
                System.out.println("Giga response raw:");
                System.out.println(messageText);
                String rawJsonResponse = parseUserPrompt.initPromptParser(messageText);
                System.out.println(rawJsonResponse);
                User user = JsonUserParser.extractUserFromGigaResponse(rawJsonResponse);
                user.setId((update.getMessage().getFrom().getUserName()).hashCode());
                System.out.println("Parsed User:");
                System.out.println(user.toString());

                messageSender.sendMessage(chatId, user.toString());
//                MedicalAnalysisResult result = new MedicalAnalysisResult();
//                result.setMedical(true);
//
//                if (result.isMedical()) {
//                    int userId = update.getMessage().getFrom().getUserName().hashCode();
//                    dataController.saveHealthData(result.getHealthData());
//                    messageSender.sendMessage(chatId, Answers.REGISTRATION_SUCCESSFUL.getMessage());
//
//                    registrationContext.completeRegistration(chatId);
//                } else {
//                    messageSender.sendMessage(chatId, Answers.INVALID_INPUT_DATA.getMessage());
//                }
            } catch (Exception e) {
                log.error("Error during registration processing", e);
                try {
                    messageSender.sendMessage(chatId, Answers.REGISTRATION_ERROR.getMessage());
                } catch (TelegramApiException ex) {
                    log.error("Failed to send error message", ex);
                }
            }
        }
    }
}
