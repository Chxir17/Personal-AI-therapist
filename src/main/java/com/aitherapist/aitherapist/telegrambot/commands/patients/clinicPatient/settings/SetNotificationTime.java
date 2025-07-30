package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient.settings;

import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.services.NotificationServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class SetNotificationTime implements ICommand {
    private final IMessageSender messageSender;

    public SetNotificationTime(IMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);

        registrationContext.setStatus(userId, Status.SET_NOTIFICATION_TIME);

        messageSender.sendMessageAndSetToList( SendMessage.builder()
                .chatId(chatId.toString())
                .text("⏰ Введите время в формате HH:mm (например, 09:30):")
                .build(), registrationContext, TelegramIdUtils.extractUserId(update));
        return null;
    }
}