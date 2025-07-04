package com.aitherapist.aitherapist.telegrambot.messageshandler;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface IMessageHandler {
    boolean canHandle(String messageText);
    void handle(Update update);
}
