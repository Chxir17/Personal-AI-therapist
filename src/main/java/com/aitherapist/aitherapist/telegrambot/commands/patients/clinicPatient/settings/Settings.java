package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient.settings;

import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.domain.model.entities.MedicalNormalData;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.services.NotificationServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    private final ITelegramExecutor telegramExecutor;
    private final RegistrationContext registrationContext;

    @Autowired
    public Settings(UserServiceImpl userService,
                    NotificationServiceImpl notificationService,
                    @Lazy ITelegramExecutor telegramExecutor, RegistrationContext registrationContext) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.telegramExecutor = telegramExecutor;
        this.registrationContext = registrationContext;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);

        if (update.hasCallbackQuery()) {
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            telegramExecutor.deleteMessage(chatId.toString(), messageId);
        }

        User user = userService.fetchUserByTelegramId(userId);
        if (user == null) {
            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("❌ Пользователь не найден")
                    .build();
        }

        registrationContext.setStatus(userId, Status.NOTIFICATION_SETTINGS);
        return showSettingsMenu(chatId, user, userId);
    }

    private SendMessage showSettingsMenu(Long chatId, User user, Long userId) {
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

        MedicalNormalData medicalData = registrationContext.getMedicalNormalData(userId);
        messageText += "\n" + medicalData.formatMedicalNormsForTelegram();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(
                InlineKeyboardButton.builder()
                        .text(notificationsEnabled ? "🔕 Выключить уведомления" : "🔔 Включить уведомления")
                        .callbackData("/toggleNotification")
                        .build()
        ));

        rows.add(List.of(
                InlineKeyboardButton.builder()
                        .text("⏰ Установить время")
                        .callbackData("/setNotificationTime")
                        .build()
        ));

        rows.add(List.of(
                InlineKeyboardButton.builder()
                        .text("📝 Изменить текст")
                        .callbackData("/setNotificationMessage")
                        .build()
        ));

        InlineKeyboardMarkup keyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(messageText)
                .parseMode("Markdown")
                .replyMarkup(keyboardMarkup)
                .build();
    }
}