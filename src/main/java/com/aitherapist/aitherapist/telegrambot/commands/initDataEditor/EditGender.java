package com.aitherapist.aitherapist.telegrambot.commands.initDataEditor;

import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.Status;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class EditGender implements ICommand {
    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long chatId = TelegramIdUtils.getChatId(update);
        Long userId = TelegramIdUtils.extractUserId(update);

        registrationContext.setStatus(userId, Status.EDIT_GENDER);

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text("Измените пол на:")
                .build();
    }
    private Long getChatId(Update update) {
        return update.hasCallbackQuery() ?
                update.getCallbackQuery().getMessage().getChatId() :
                update.getMessage().getChatId();
    }
}