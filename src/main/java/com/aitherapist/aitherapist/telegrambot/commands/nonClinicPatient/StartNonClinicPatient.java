package com.aitherapist.aitherapist.telegrambot.commands.nonClinicPatient;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.Status;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class StartNonClinicPatient implements ICommand {
    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        registrationContext.setStatus(userId, Status.SECOND_PART_REGISTRATION);
        return new SendMessage(String.valueOf(chatId),Answers.WRITE_MEDICAL_INFO.getMessage());
    }
}
