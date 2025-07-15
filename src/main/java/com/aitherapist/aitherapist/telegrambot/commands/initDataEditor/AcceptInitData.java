package com.aitherapist.aitherapist.telegrambot.commands.initDataEditor;

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
public class AcceptInitData implements ICommand {
    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        long chatId = update.getMessage().getChatId();
        Map<String, String> buttons = new HashMap<>();
        buttons.put("Доктор", "/startDoctor");
        buttons.put("Пациент клиники", "/startClinicPatient");
        buttons.put("Пациент не привязанный клиники", "/startBotPatient");

        InlineKeyboardMarkup replyKeyboardDoctor = InlineKeyboardFactory.createInlineKeyboard(buttons, 3);
        registrationContext.startRegistration(chatId);

        return SendMessage.builder()
              .chatId(String.valueOf(chatId))
              .text("Выберите роль")
              .replyMarkup(replyKeyboardDoctor)
              .build();
    }
}
