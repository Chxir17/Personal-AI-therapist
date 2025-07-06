package com.aitherapist.aitherapist.telegrambot.messageshandler;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * IMessageHandler -
 */
public interface IHandler {
    boolean canHandle(String messageText);
    void handle(Update update);
}
