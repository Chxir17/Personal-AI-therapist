package com.aitherapist.aitherapist.telegrambot.commands.medicalDataEditor;

import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class EditWeight implements ICommand {
    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        Long chatId = TelegramIdUtils.getChatId(update);
        Long userId = TelegramIdUtils.extractUserId(update);

        registrationContext.setStatus(userId, Status.EDIT_WEIGHT);

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text("Измените вес (в килограммах):")
                .build();
    }
}