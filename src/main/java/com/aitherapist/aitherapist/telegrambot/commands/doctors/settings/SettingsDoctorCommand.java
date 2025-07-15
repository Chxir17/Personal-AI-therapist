package com.aitherapist.aitherapist.telegrambot.commands.doctors.settings;

import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class SettingsDoctorCommand implements ICommand {
    private IMessageSender messageSender;

    @Autowired
    public SettingsDoctorCommand(TelegramMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        long chatId = TelegramIdUtils.getChatId(update);

        InlineKeyboardMarkup commands = InlineKeyboardFactory.createDoctorSettingsKeyboard();

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Выберите команду")
                .replyMarkup(commands)
                .build();
    }
}