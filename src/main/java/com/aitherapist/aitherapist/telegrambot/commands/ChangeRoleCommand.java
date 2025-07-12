package com.aitherapist.aitherapist.telegrambot.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ChangeRoleCommand implements ICommand {

    @Override
    public SendMessage apply(Update update) throws TelegramApiException {
        return null;
    }
}
