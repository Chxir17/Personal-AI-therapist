package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient.settings;

import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.services.NotificationServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class Settings implements ICommand {

    private final UserServiceImpl userService;
    private final NotificationServiceImpl notificationService;

    @Autowired
    public Settings(UserServiceImpl userService, NotificationServiceImpl notificationService) {
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


        registrationContext.setStatus(userId, Status.NOTIFICATION_SETTINGS);
        return showSettingsMenu(chatId, user);
    }

    private SendMessage showSettingsMenu(Long chatId, User user) {
        boolean notificationsEnabled = notificationService.getNotificationEnabled(user);
        LocalTime notificationTime = notificationService.getNotificationTime(user);
        String customMessage = notificationService.getMessage(user);

        String messageText = String.format(
                "⚙️ *Настройки уведомлений*\n\n" +
                        "🔔 Уведомления: %s\n" +
                        "⏰ Время напоминания: %s\n" +
                        "📝 Текст напоминания: %s\n\n" +
                        "Вы можете изменить эти настройки:",
                notificationsEnabled ? "ВКЛ" : "ВЫКЛ",
                notificationTime != null ? notificationTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "не установлено",
                customMessage != null ? customMessage : "не установлен"
        );

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text(notificationsEnabled ? "🔕 Выключить уведомления" : "🔔 Включить уведомления")
                .callbackData("/toggleNotification")
                .build());

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder()
                .text("⏰ Установить время")
                .callbackData("/setNotificationTime")
                .build());

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(InlineKeyboardButton.builder()
                .text("📝 Изменить текст")
                .callbackData("/setNotificationMessage")
                .build());

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        keyboardMarkup.setKeyboard(rows);

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(messageText)
                .parseMode("Markdown")
                .replyMarkup(keyboardMarkup)
                .build();
    }
}