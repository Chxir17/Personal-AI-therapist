package com.aitherapist.aitherapist.telegrambot.commands.users;

import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartUsers implements ICommand {
    @Override
    public boolean verify(Update update) throws TelegramApiException {
        return false;
    }
}
