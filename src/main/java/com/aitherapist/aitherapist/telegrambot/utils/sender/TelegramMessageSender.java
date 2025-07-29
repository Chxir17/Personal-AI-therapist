package com.aitherapist.aitherapist.telegrambot.utils.sender;

import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramMessageSender implements IMessageSender {
    private final ITelegramExecutor telegramExecutor;

    public TelegramMessageSender(@Lazy ITelegramExecutor telegramExecutor) {
        this.telegramExecutor = telegramExecutor;
    }

    @Override
    public void sendMessage(long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        telegramExecutor.execute(message);
    }

    @Override
    public void sendMessage(SendMessage sendMessage) throws TelegramApiException {
        telegramExecutor.execute(sendMessage);
    }

    @Override
    public void sendMessageAndSetToList(SendMessage sendMessage, RegistrationContext registrationContext, Long userId) throws TelegramApiException {
        Message message = telegramExecutor.execute(sendMessage);
        registrationContext.setMessageToDelete(userId, message.getMessageId());
    }


}
