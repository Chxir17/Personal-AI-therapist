package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.CommandAccess;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Objects;

@CommandAccess(allowedRoles = {Roles.CLINIC_PATIENT, Roles.BOT_PATIENT}, requiresRegistration = true)
@Component
public class HealthHistory implements ICommand {

    private final PatientServiceImpl patientService;

    public HealthHistory(PatientServiceImpl patientService) {
        this.patientService = patientService;
    }

    @Override
    @Transactional(readOnly = true)
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) {
        Long userId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);
        Patient patient = patientService.findById(userId);

        List<DailyHealthData> healthData = patientService.getPatientDailyHealthData(userId);
        //healthData.sort(Comparator.comparingLong(DailyHealthData::getId).reversed());

        StringBuilder message = new StringBuilder();
        message.append("📆 История ваших показателей:\n\n");

        if (healthData.isEmpty()) {
            message.append("У вас пока нет сохранённых данных о здоровье");
        } else {
            int counter = 1;
            for (DailyHealthData data : healthData) {
                message.append(formatHealthData(data, counter));
                counter++;
            }
            message.append("\n🔍 *Статистика за весь период:*\n");
            message.append(calculateStatistics(healthData));
        }

        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createPatientDefaultKeyboard(patient);

        if (update.hasCallbackQuery()) {
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            try {
                telegramExecutor.editMessageText(
                        chatId.toString(),
                        messageId,
                        message.toString(),
                        keyboard
                );
                return null;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(message.toString())
                .parseMode("MarkdownV2")
                .replyMarkup(keyboard)
                .build();
    }

    private String formatHealthData(DailyHealthData data, int counter) {
        return String.format(
                "🆔 ID записи: %d\n" +
                        "🌡 Температура: %s\n" +
                        "❤️ Пульс: %s\n" +
                        "🩸 Давление: %s\n" +
                        "😴 Сон: %s\n" +
                        "🧪 Кислород: %s\n\n",
                counter,
                data.getTemperature() != null ? String.format("%.1f°C", data.getTemperature()) : "нет данных",
                data.getPulse() != null ? data.getPulse() + " уд/мин" : "нет данных",
                data.getPressure() != null ? data.getPressure() : "нет данных",
                data.getHoursOfSleepToday() != null ? String.format("%.1f часов", data.getHoursOfSleepToday()) : "нет данных",
                data.getBloodOxygenLevel() != null ? String.format("%.1f%%", data.getBloodOxygenLevel()) : "нет данных"
        );
    }

    private String calculateStatistics(List<DailyHealthData> data) {

        DoubleSummaryStatistics tempStats = data.stream()
                .map(DailyHealthData::getTemperature)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        IntSummaryStatistics pulseStats = data.stream()
                .map(DailyHealthData::getPulse)
                .filter(Objects::nonNull)
                .mapToInt(Long::intValue)
                .summaryStatistics();
        DoubleSummaryStatistics sleepStats = data.stream()
                .map(DailyHealthData::getHoursOfSleepToday)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        DoubleSummaryStatistics oxygenStats = data.stream()
                .map(DailyHealthData::getBloodOxygenLevel)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        return String.format(
                "🌡 Температура: %s\n" +
                        "❤️ Пульс: %s\n" +
                        "😴 Сон: %s\n" +
                        "🧪 Кислород: %s",
                formatStats(tempStats, "°C"),
                formatStats(pulseStats, "уд/мин"),
                formatStats(sleepStats, "ч"),
                formatStats(oxygenStats, "%%")
        );
    }

    private String formatStats(DoubleSummaryStatistics stats, String unit) {
        if (stats.getCount() == 0) {
            return "нет данных";
        }
        return String.format("макс %.1f%s / мин %.1f%s / сред %.1f%s",
                stats.getMax(), unit,
                stats.getMin(), unit,
                stats.getAverage(), unit);
    }

    private String formatStats(IntSummaryStatistics stats, String unit) {
        if (stats.getCount() == 0) {
            return "нет данных";
        }
        return String.format("макс %d%s / мин %d%s / сред %.1f%s",
                stats.getMax(), unit,
                stats.getMin(), unit,
                stats.getAverage(), unit);
    }
}