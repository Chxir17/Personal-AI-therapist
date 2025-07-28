package com.aitherapist.aitherapist.telegrambot.commands;

import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * ICommand - interface for commands in telegram. pin/start... commands use ICommand (methods apply).
 */
public interface ICommand {
    @Nullable
    SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException;
}
