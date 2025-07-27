package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class GetLastMessageFromDoctor implements ICommand {
    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        //FIXME тоже надо как то модифицировать архитектуру
        return null;
    }
}
