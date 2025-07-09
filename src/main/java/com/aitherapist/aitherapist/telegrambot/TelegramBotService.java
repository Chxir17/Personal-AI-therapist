package com.aitherapist.aitherapist.telegrambot;

import com.aitherapist.aitherapist.Consts;
import com.aitherapist.aitherapist.telegrambot.messageshandler.MessagesHandler;
import com.aitherapist.aitherapist.telegrambot.utils.Answers;
import com.aitherapist.aitherapist.telegrambot.utils.BotProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.springframework.context.annotation.Lazy;

/**
 * TelegramBotService - main class with main methods onUpdateReceived.
 * when user sends message, it arrives to onUpdateReceived.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot implements ITelegramExecutor  {
    private final BotProperties botProperties;
    private final CommandsHandler commandsHandler;
    private final @Lazy MessagesHandler messagesHandler;

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
     * Check message. if command -> call handle command and update state.
     * else handle message
     * @param update - full information about users.
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            if (update.getMessage().getText().startsWith("/")) {
                try {
                    sendMessage(commandsHandler.handleCommand(update));
                }
                catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                if (messagesHandler.canHandle(update.getMessage().getText())) {
                    try {
                        messagesHandler.handle(update);
                    } catch (TelegramApiException | JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        sendMessage(new SendMessage(chatId, Answers.IS_NOT_MEDICAL_INFORMATION.getMessage()));}
                    catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * sendMessage - execute message. (Send to user)
     * @param sendMessage
     */
    public void sendMessage(@Nullable SendMessage sendMessage) throws TelegramApiException {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Send message error.", e);
            throw e;
        }
    }

    @Override
    public void execute(SendMessage sendMessage) throws TelegramApiException {
        try {
            super.execute(sendMessage);  // Call to parent class method
        } catch (TelegramApiException e) {
            log.error("Send message error.", e);
            throw e;
        }
    }
}
