package com.aitherapist.aitherapist.telegrambot.utils.sender;

import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface IMessageSender {
    void sendMessage(long chatId, String text) throws TelegramApiException;
    void sendMessage(SendMessage sendMessage) throws TelegramApiException;
    void sendMessageAndSetToList(SendMessage sendMessage, RegistrationContext registrationContext, Long userId) throws TelegramApiException;
}