package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.List;

@Component
public class HealthHistory implements ICommand {

    private final PatientServiceImpl patientService;

    @Autowired
    public HealthHistory(PatientServiceImpl patientService) {
        this.patientService = patientService;
    }

    @Override
    @Transactional(readOnly = true)
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);

        List<DailyHealthData> healthData = patientService.getPatientDailyHealthData(userId);

        StringBuilder message = new StringBuilder();
        message.append("📆 *История ваших показателей:*\n\n");

        for (DailyHealthData data : healthData) {
            message.append(String.format(
                    "🌡 Температура: *%.1f°C*\n" +
                            "❤️ Пульс: *%d уд/мин*\n" +
                            "🩸 Давление: *%s*\n" +
                            "😴 Сон: *%.1f часов*\n" +
                            "🧪 Кислород: *%.1f%%*\n\n",
                    data.getTemperature(),
                    data.getPulse(),
                    data.getPressure() != null ? data.getPressure() : "нет данных",
                    data.getHoursOfSleepToday(),
                    data.getBloodOxygenLevel()
            ));
        }

        if (!healthData.isEmpty()) {
            message.append("\n🔍 *Статистика:*\n");
            message.append(getMinMaxStats(healthData));
        }

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(message.toString())
                .parseMode("MarkdownV2")
                .replyMarkup(InlineKeyboardFactory.createPatientDefaultKeyboard())
                .build();
    }

    private String getMinMaxStats(List<DailyHealthData> data) {
        DoubleSummaryStatistics tempStats = data.stream()
                .mapToDouble(DailyHealthData::getTemperature)
                .summaryStatistics();

        IntSummaryStatistics pulseStats = data.stream()
                .mapToInt(d -> d.getPulse().intValue())
                .summaryStatistics();

        DoubleSummaryStatistics sleepStats = data.stream()
                .mapToDouble(DailyHealthData::getHoursOfSleepToday)
                .summaryStatistics();

        return String.format(
                "🌡 Температура: макс %.1f°C / мин %.1f°C\n" +
                        "❤️ Пульс: макс %d / мин %d\n" +
                        "😴 Сон: макс %.1fч / мин %.1fч",
                tempStats.getMax(), tempStats.getMin(),
                pulseStats.getMax(), pulseStats.getMin(),
                sleepStats.getMax(), sleepStats.getMin()
        );
    }
}