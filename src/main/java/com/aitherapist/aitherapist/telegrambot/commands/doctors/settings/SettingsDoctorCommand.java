package com.aitherapist.aitherapist.telegrambot.commands.doctors.settings;

import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class SettingsDoctorCommand implements ICommand {

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        long chatId = TelegramIdUtils.getChatId(update);
        String messageText = "✨ Доступные действия ✨";
        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createDoctorSettingsKeyboard();
        if (update.hasCallbackQuery()) {
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            try {
                telegramExecutor.editMessageText(String.valueOf(chatId), messageId, messageText, keyboard);
                return null;
            } catch (TelegramApiException e) {
               e.printStackTrace();
            }
        }

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(messageText)
                .replyMarkup(keyboard)
                .build();
    }
}