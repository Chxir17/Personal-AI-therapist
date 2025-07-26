package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient.settings;

import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.services.NotificationServiceImpl;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ToggleNotifications implements ICommand {

    private final UserServiceImpl userService;
    private final NotificationServiceImpl notificationService;
    private PatientServiceImpl patientService;

    @Autowired
    public ToggleNotifications(PatientServiceImpl patientService, UserServiceImpl userService, NotificationServiceImpl notificationService) {
        this.patientService = patientService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);

        User user = userService.fetchUserByTelegramId(userId);
        if (user == null) {
            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("❌ Пользователь не найден")
                    .build();
        }

        boolean currentStatus = notificationService.getNotificationEnabled(user);
        notificationService.setNotificationEnabled(user, !currentStatus);

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text("🔔 Уведомления " + (!currentStatus ? "включены" : "выключены"))
                .replyMarkup(InlineKeyboardFactory.createPatientDefaultKeyboard(patientService.findById(userId)))
                .build();
    }
}