package com.aitherapist.aitherapist.telegrambot.utils.sender;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface IMessageSender {
    void sendMessage(long chatId, String text) throws TelegramApiException;
}