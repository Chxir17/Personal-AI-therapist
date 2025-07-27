package com.aitherapist.aitherapist.telegrambot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface ITelegramExecutor {
    void execute(SendMessage message) throws TelegramApiException;
    void deleteMessage(String chatId, Integer messageId);
}
