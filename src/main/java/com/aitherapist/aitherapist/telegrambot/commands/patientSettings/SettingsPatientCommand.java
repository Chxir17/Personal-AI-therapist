package com.aitherapist.aitherapist.telegrambot.commands.patientSettings;

import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
public class SettingsPatientCommand implements ICommand {
    private IMessageSender messageSender;
    @Autowired
    public SettingsPatientCommand(TelegramMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        long chatId = update.getMessage().getChatId();
        Map<String, String> buttons = new HashMap<>();
        buttons.put("Изменить данные об аккаунте", "/changePatientAccountData");
        buttons.put("Сменить роль","/changeRole");
        InlineKeyboardMarkup commands = InlineKeyboardFactory.createInlineKeyboard(buttons, 2);

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Выберите Комманду")
                .replyMarkup(commands)
                .build();

    }
}
