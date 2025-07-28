package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class AcceptDoctorData implements ICommand {
    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        long chatId = TelegramIdUtils.getChatId(update);
        InlineKeyboardMarkup replyKeyboardDoctor = InlineKeyboardFactory.createDoctorDefaultKeyboard();
        registrationContext.startRegistration(chatId);

        if (update.hasCallbackQuery()) {
            return null;
        }

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Выберите роль")
                .replyMarkup(replyKeyboardDoctor)
                .build();
    }
}