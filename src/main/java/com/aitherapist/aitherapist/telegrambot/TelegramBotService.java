package com.aitherapist.aitherapist.telegrambot;

import com.aitherapist.aitherapist.Consts;
import com.aitherapist.aitherapist.telegrambot.messageshandler.MessagesHandler;
import com.aitherapist.aitherapist.telegrambot.utils.Answers;
import com.aitherapist.aitherapist.telegrambot.utils.BotProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * TelegramBotService - main class with main methods onUpdateReceived.
 * when user sends message, it arrives to onUpdateReceived.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot {
    private final BotProperties botProperties;
    private final CommandsHandler commandsHandler;
    private final MessagesHandler messagesHandler;

    /**
     * getBotToken - get information (bot token) from application.yml
     * @return
     */
    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }

    /**
     * Check message. if command -> call handle command and update state. else handle message
     * @param update - full information about users.
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            if (update.getMessage().getText().startsWith("/")) {
                sendMessage(commandsHandler.handleCommand(update));
            } else {
                if (messagesHandler.canHandle(update.getMessage().getText())) {
                    messagesHandler.handle(update);
                } else {
                    sendMessage(new SendMessage(chatId, Answers.IS_NOT_MEDICAL_INFORMATION.getMessage()));
                }
            }
        }
    }

    /**
     * sendMessage - execute message. (Send to user)
     * @param sendMessage
     */
    private void sendMessage(SendMessage sendMessage) {
        if (sendMessage == null) {
            return;
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
