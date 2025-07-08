package com.aitherapist.aitherapist.telegrambot.messageshandler;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * IMessageHandler -
 */
public interface IHandler {
    boolean canHandle(String messageText);
    void handle(Update update) throws TelegramApiException;
}
