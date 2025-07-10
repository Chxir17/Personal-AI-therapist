package com.aitherapist.aitherapist.telegrambot.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * ICommand - interface for commands in telegram. pin/start... commands use ICommand (methods apply).
 */
public interface ICommand {
    boolean verify(Update update) throws TelegramApiException;
}
