package com.aitherapist.aitherapist.telegrambot.commands.medicalDataEditor;

import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class AbstractEditCommand implements ICommand {
    private final TelegramMessageSender telegramMessageSender;
    private final String prompt;
    private final Status status;

    protected AbstractEditCommand(TelegramMessageSender telegramMessageSender, String prompt, Status status) {
        this.telegramMessageSender = telegramMessageSender;
        this.prompt = prompt;
        this.status = status;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        Long chatId = TelegramIdUtils.getChatId(update);
        Long userId = TelegramIdUtils.extractUserId(update);

        if (registrationContext.getMessageToDelete(userId) != null) {
            for(Integer message: registrationContext.getMessageToDelete(userId)) {
                telegramExecutor.deleteMessage(chatId.toString(), message);
            }
        }

        registrationContext.setStatus(userId, status);

        telegramMessageSender.sendMessageAndSetToList(SendMessage.builder()
                .chatId(chatId.toString())
                .text(prompt)
                .build(), registrationContext, userId);

        return null;
    }
}