package com.aitherapist.aitherapist.telegrambot.commands.patients;

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
public class ChangePatientAccountData implements ICommand {

    @Override
    public SendMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();

        Map<String, String> buttons = Map.of(
                "Сменить возраст", "/changeAge",
                "Сменить пол", "/changeGender",
                "Сменить имя", "/changeName",
                "Сменить рост и вес", "/changeHeightWeight",
                "Сменить вредные привычки", "/changeHabits"
        );

        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createInlineKeyboard(buttons, 1);
//FIXME всё таки я бы остановился бы на промте потому что во первых плодить много кнопок не красиво и во вторых бот чтобы
// был чуть умнее типо чтобы из контекста мог вычленять информацию
        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Что вы хотите изменить в аккаунте?")
                .replyMarkup(keyboard)
                .build();
    }
}