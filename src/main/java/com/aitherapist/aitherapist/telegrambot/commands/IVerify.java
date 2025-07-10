package com.aitherapist.aitherapist.telegrambot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface IVerify {
    boolean verify(Update update) throws TelegramApiException;

}
