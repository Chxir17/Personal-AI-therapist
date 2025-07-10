package com.aitherapist.aitherapist.telegrambot.commands.users;

import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.commands.IVerify;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartUsers implements IVerify {
    @Override
    public boolean verify(Update update) throws TelegramApiException {
        return false;
    }
}
