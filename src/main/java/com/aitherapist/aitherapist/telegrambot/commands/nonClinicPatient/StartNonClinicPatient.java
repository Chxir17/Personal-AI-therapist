package com.aitherapist.aitherapist.telegrambot.commands.nonClinicPatient;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.commands.IVerify;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.SendMessageUser;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartNonClinicPatient implements ICommand {
    @Override
    public SendMessage apply(Update update) throws TelegramApiException {
        long chatId = update.getMessage().getChatId();
        return new SendMessage(String.valueOf(chatId),Answers.INITIAL_MESSAGE_ABOUT_USER.getMessage());
    }
}
