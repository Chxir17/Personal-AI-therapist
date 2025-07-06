package com.aitherapist.aitherapist.telegrambot.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * ICommand - interface for commands in telegram. pin/start... commands use ICommand (methods apply).
 */
public interface ICommand {
    SendMessage apply(Update update);
}
