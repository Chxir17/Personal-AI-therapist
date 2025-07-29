package com.aitherapist.aitherapist.telegrambot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

/**
 * Verification - class for verification user.
 */
@Component
public class Verification {

    /**
     * check is user number equal
     */
    public static Boolean verify(Update update, String userTelephoneNumber) {
        if (update.hasMessage() && update.getMessage().hasContact()) {
            String contactPhoneNumber = update.getMessage().getContact().getPhoneNumber();
            String normalizedContactNumber = normalizePhoneNumber(contactPhoneNumber);
            String normalizedUserNumber = normalizePhoneNumber(userTelephoneNumber);
            return normalizedContactNumber.equals(normalizedUserNumber);
        }
        return false;
    }


    public static ReplyKeyboardMarkup createContactRequestKeyboard() {
        KeyboardButton contactButton = new KeyboardButton("Поделиться номером");
        contactButton.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(contactButton);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setKeyboard(List.of(row));
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        return keyboard;
    }



    private static String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return "";
        }
        return phoneNumber.replaceAll("[^0-9]", "");
    }

}