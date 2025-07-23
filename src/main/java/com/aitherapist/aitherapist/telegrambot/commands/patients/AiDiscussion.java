package com.aitherapist.aitherapist.telegrambot.commands.patients;

import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class AiDiscussion implements ICommand {

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {

        return null;
    }
}
