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
                    @Lazy ITelegramExecutor telegramExecutor,
                    RegistrationContext registrationContext) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.telegramExecutor = telegramExecutor;
        this.registrationContext = registrationContext;
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


        String messageText = buildSettingsMessage(user, userId);
        InlineKeyboardMarkup keyboard = buildSettingsKeyboard(user);

        if (update.hasCallbackQuery()) {
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            try {

                telegramExecutor.editMessageText(
                        chatId.toString(),
                        messageId,
                        messageText,
                        keyboard
                );
                return null;
            } catch (TelegramApiException e) {
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(messageText)
                        .parseMode("HTML")
                        .replyMarkup(keyboard)
                        .build();
            }
        }

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(messageText)
                .parseMode("HTML")
                .replyMarkup(keyboard)
                .build();
    }

    private String buildSettingsMessage(User user, Long userId) {
        boolean notificationsEnabled = notificationService.getNotificationEnabled(user);
        LocalTime notificationTime = notificationService.getNotificationTime(user);
        String customMessage = notificationService.getMessage(user);
        MedicalNormalData medicalData = registrationContext.getMedicalNormalData(userId);

        return "✨ Ваши персональные настройки и нормативы ✨\n\n" +
                "⚙️ Настройки уведомлений\n\n" +
                "🔔  Статус: " + (notificationsEnabled ? "ВКЛ ✅" : "ВЫКЛ ❌") + "\n" +
                "⏰  Время: " + (notificationTime != null ?
                notificationTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "не установлено") + "\n" +
                "📝  Текст: " + (customMessage != null ? customMessage : "не установлен") + "\n\n\n" +
                "🩺 Ваши медицинские нормативы\n" +
                "💤  Сон: " + String.format("%.1f", medicalData.getHoursOfSleepToday()) + " ч/сутки\n" +
                "❤️  Пульс: " + medicalData.getPulse() + " уд/мин\n" +
                "🩸  Давление: " + medicalData.getPressure() + "\n\n\n" +
                "Эти показатели рассчитаны специально для вас 💙";
    }

    private InlineKeyboardMarkup buildSettingsKeyboard(User user) {
        boolean notificationsEnabled = notificationService.getNotificationEnabled(user);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(
                InlineKeyboardButton.builder()
                        .text("⏰ Изменить время")
                        .callbackData("/setNotificationTime")
                        .build(),
                InlineKeyboardButton.builder()
                        .text(notificationsEnabled ? "🔕 Выключить" : "🔔 Включить")
                        .callbackData("/toggleNotification")
                        .build()
        ));

        rows.add(List.of(
                InlineKeyboardButton.builder()
                        .text("📝 Изменить текст")
                        .callbackData("/setNotificationMessage")
                        .build(),
                InlineKeyboardButton.builder()
                        .text("✏️ Редактировать профиль")
                        .callbackData("/editPatientMedicalData")
                        .build()
        ));

        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }
}