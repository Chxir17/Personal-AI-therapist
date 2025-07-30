package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient.settings;

import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.services.NotificationServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class SetNotificationMessage implements ICommand {
    private final IMessageSender messageSender;

    public SetNotificationMessage(IMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);

        registrationContext.setStatus(userId, Status.SET_NOTIFICATION_MESSAGE);

        messageSender.sendMessageAndSetToList(SendMessage.builder()
                .chatId(chatId.toString())
                .text("📝 Введите новый текст уведомления:")
                .build(), registrationContext, TelegramIdUtils.extractUserId(update));
        return null;
    }
}