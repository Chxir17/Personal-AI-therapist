package com.aitherapist.aitherapist.telegrambot.commands;

import com.aitherapist.aitherapist.telegrambot.utils.Answers;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class ChooseRoleCommand implements ICommand{

    private IMessageSender messageSender;

    @Override
    public SendMessage apply(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        long chatId = update.getMessage().getChatId();
        ReplyKeyboardMarkup replyKeyboardMarkup = createDoctorKeyboard();

        if(!userRegistrationService.isSignUp(Math.toIntExact(userId))) {
            registrationContext.startRegistration(chatId);
            return new SendMessage(String.valueOf(chatId), Answers.INITIAL_MESSAGE_ABOUT_USER.getMessage());
        }
        return new SendMessage(String.valueOf(chatId), Answers.START_MESSAGE.getMessage() );
        return null;
    }

    public ReplyKeyboardMarkup createDoctorKeyboard() {
        KeyboardButton contactButton = new KeyboardButton("Пациент");
        contactButton.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(contactButton);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setKeyboard(List.of(row));
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        return keyboard;
    }

    public static ReplyKeyboardMarkup createPatientKeyboard() {
        KeyboardButton contactButton = new KeyboardButton("Доктор");
        contactButton.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(contactButton);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setKeyboard(List.of(row));
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        return keyboard;
    }
}
