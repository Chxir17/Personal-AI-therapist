package com.aitherapist.aitherapist.telegrambot;

import com.aitherapist.aitherapist.telegrambot.messageshandler.MessagesHandler;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot implements ITelegramExecutor {
    private final BotProperties botProperties;
    private final CommandsHandler commandsHandler;
    private final @Lazy MessagesHandler messagesHandler;

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            if (update.getMessage().getText().startsWith("/start")) {
                update.getCallbackQuery().setData(update.getMessage().getText());
            }
            else {
                try {
                    messagesHandler.handle(update);
                } catch (TelegramApiException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            Long chatId = update.getMessage().getChatId();

            if (callBackData.startsWith("/")) {
                try {
                    sendMessage(commandsHandler.handleCommand(update));
                } catch (TelegramApiException e) {
                    log.error("Error handling command", e);
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    messagesHandler.handle(update);
                } catch (TelegramApiException | JsonProcessingException e) {
                    log.error("Error handling message", e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void sendMessage(@Nullable SendMessage sendMessage) throws TelegramApiException {
        if (sendMessage != null) {
            execute(sendMessage);
        }
    }

    @Override
    public void execute(SendMessage sendMessage) throws TelegramApiException {
        try {
            super.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Send message error", e);
            throw e;
        }
    }
}