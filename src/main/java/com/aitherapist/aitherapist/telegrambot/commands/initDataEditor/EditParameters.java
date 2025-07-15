package com.aitherapist.aitherapist.telegrambot.commands.initDataEditor;

import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class EditParameters implements ICommand {

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();

        Map<String, String> buttons = new LinkedHashMap<>();
        buttons.put("Изменить имя", "/editName");
        buttons.put("Изменить дату рождения", "/editBirthDate");
        buttons.put("Изменить пол", "/editGender");

        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createInlineKeyboard(buttons, 1);

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text("Что вы хотите изменить?")
                .replyMarkup(keyboard)
                .build();
    }
}