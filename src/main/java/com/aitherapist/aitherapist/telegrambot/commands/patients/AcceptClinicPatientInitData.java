package com.aitherapist.aitherapist.telegrambot.commands.patients;

import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
public class AcceptClinicPatientInitData implements ICommand {
    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        long chatId = update.getMessage().getChatId();
        Map<String, String> buttons = new HashMap<>();
        buttons.put("Получить последние сообщения от доктора", "/getLastMessageDoctor");
        buttons.put("Ввести актуальные измерения", "/writeActualData");
        buttons.put("Отправить сообщение доктору","/sendMessageDoctor");
        buttons.put("Настройки","/settingsPatient"); //перенести в профиль
        buttons.put("Профиль", "/profile");
        InlineKeyboardMarkup commands = InlineKeyboardFactory.createInlineKeyboard(buttons, 2);
        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Выберите команду")
                .replyMarkup(commands)
                .build();
    }
}
