package com.aitherapist.aitherapist.telegrambot.commands;

import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class BotPatientCommand implements ICommand{
    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) {
        long chatId = update.getMessage().getChatId();
        return new SendMessage(String.valueOf(chatId), "пациент не привязанный к клинике" );
    }
}
