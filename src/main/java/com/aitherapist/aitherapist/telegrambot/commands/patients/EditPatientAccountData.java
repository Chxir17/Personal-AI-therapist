package com.aitherapist.aitherapist.telegrambot.commands.patients;

import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class EditPatientAccountData implements ICommand {

    private final IMessageSender messageSender;

    @Autowired
    public EditPatientAccountData(IMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long chatId = TelegramIdUtils.getChatId(update);

        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createEditClinicPatientData();

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text("Что вы хотите изменить в медицинских данных?")
                .replyMarkup(keyboard)
                .build();
    }
}