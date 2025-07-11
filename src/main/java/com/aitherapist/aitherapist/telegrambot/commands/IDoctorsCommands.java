package com.aitherapist.aitherapist.telegrambot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface IDoctorsCommands {
    void apply(Update update);
    void sendMessageToUser(Long userId, String message) throws TelegramApiException;

}
