package com.aitherapist.aitherapist.telegrambot.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DoctorCommand implements ICommand{
    @Override
    public SendMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        return new SendMessage(String.valueOf(chatId), "доктор" );    }
}
