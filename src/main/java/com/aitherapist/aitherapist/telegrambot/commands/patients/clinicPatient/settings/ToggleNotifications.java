package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient.settings;

import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.services.NotificationServiceImpl;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ToggleNotifications implements ICommand {
    private final UserServiceImpl userService;
    private final NotificationServiceImpl notificationService;
    private final PatientServiceImpl patientService;
    private final IMessageSender messageSender;


    @Autowired
    public ToggleNotifications(PatientServiceImpl patientService,
                               UserServiceImpl userService,
                               NotificationServiceImpl notificationService, IMessageSender messageSender) {
        this.patientService = patientService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.messageSender = messageSender;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);

        User user = userService.fetchUserByTelegramId(userId);
        if (user == null) {
            messageSender.sendMessageAndSetToList( SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("❌ Пользователь не найден")
                    .build(), registrationContext, userId);
            return null;
        }

        boolean currentStatus = notificationService.getNotificationEnabled(user);
        boolean newStatus = !currentStatus;
        notificationService.setNotificationEnabled(user, newStatus);

        String statusMessage = newStatus ?
                "🔔 Уведомления включены. Вы будете получать напоминания согласно установленному расписанию." :
                "🔕 Уведомления выключены. Вы не будете получать напоминания.";


        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createPatientDefaultKeyboard(patientService.findById(userId));
        String messageText = "✨ Доступные действия ✨";
        if (update.hasCallbackQuery()) {
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            try {
                telegramExecutor.editMessageText(
                        String.valueOf(TelegramIdUtils.getChatId(update)),
                        messageId,
                        messageText,
                        keyboard
                );
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        messageSender.sendMessageAndSetToList(SendMessage.builder()
                .chatId(chatId.toString())
                .text(statusMessage)
                .build(), registrationContext, userId);
        return null;
    }
}