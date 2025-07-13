package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChangeDoctorAccountData implements ICommand {

    @Override
    public SendMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();

        Map<String, String> buttons = Map.of(
                "Сменить возраст", "/changeAge",
                "Сменить пол", "/changeGender",
                "Сменить имя", "/changeName"
        );

        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createInlineKeyboard(buttons, 1);

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Что вы хотите изменить в аккаунте?")
                .replyMarkup(keyboard)
                .build();
    }
}