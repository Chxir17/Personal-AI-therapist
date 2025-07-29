package com.aitherapist.aitherapist.telegrambot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface ITelegramExecutor {
    Message execute(SendMessage message) throws TelegramApiException;
    void deleteMessage(String chatId, Integer messageId);
    void editMessageText(String chatId, Integer messageId, String newText, InlineKeyboardMarkup keyboard) throws TelegramApiException;
    void editMessageText(String chatId, Integer messageId, String newText) throws TelegramApiException;

}
