package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
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

import java.util.Comparator;
import java.util.List;


@Component
public class GetPatientDailyData implements ICommand {

    private final PatientServiceImpl patientService;
    @Autowired
    public GetPatientDailyData(PatientServiceImpl patientService) {
        this.patientService = patientService;
    }

    @Override
    @Transactional(readOnly = true)
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        try {
            String callbackData = update.getCallbackQuery().getData();
            String[] parts = callbackData.split(" ");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Неверный формат команды");
            }

            Long patientId = Long.parseLong(parts[1]);
            Long chatId = TelegramIdUtils.getChatId(update);

            Patient patient = patientService.findById(patientId);
            if (patient == null) {
                return new SendMessage(chatId.toString(), "❌ Пациент не найден");
            }

            List<DailyHealthData> healthData = patientService.getPatientDailyHealthData(patientId);

            StringBuilder message = new StringBuilder();
            message.append(String.format("📊 <b>Ежедневные показатели пациента:</b> %s\n\n", patient.getName()));

            if (healthData.isEmpty()) {
                message.append("У пациента пока нет сохранённых данных о здоровье.");
            } else {
                for (DailyHealthData data : healthData) {
                    message.append(formatHealthData(data));
                }
            }

            SendMessage response = new SendMessage(chatId.toString(), message.toString());
            response.enableHtml(true);
            response.setReplyMarkup(InlineKeyboardFactory.createReturnToMenu());
            return response;
        } catch (NumberFormatException e) {
            Long chatId = TelegramIdUtils.getChatId(update);
            return new SendMessage(chatId.toString(), "❌ Ошибка: неверный ID пациента");
        } catch (Exception e) {
            Long chatId = TelegramIdUtils.getChatId(update);
            return new SendMessage(chatId.toString(), "❌ Произошла ошибка при получении данных");
        }
    }

    private String formatHealthData(DailyHealthData data) {
        return String.format(
                "<b>Имя:</b> %s, номер %d\n" +
                        "🌡 <b>Температура:</b> %s\n" +
                        "❤️ <b>Пульс:</b> %s\n" +
                        "🩸 <b>Давление:</b> %s\n" +
                        "😴 <b>Сон:</b> %s\n" +
                        "🧪 <b>Кислород:</b> %s\n\n",
                data.getPatient() != null ? data.getPatient().getName() : "нет данных",
                data.getId(),
                data.getTemperature() != null ? String.format("%.1f°C", data.getTemperature()) : "нет данных",
                data.getPulse() != null ? data.getPulse() + " уд/мин" : "нет данных",
                data.getPressure() != null ? data.getPressure() : "нет данных",
                data.getHoursOfSleepToday() != null ? String.format("%.1f часов", data.getHoursOfSleepToday()) : "нет данных",
                data.getBloodOxygenLevel() != null ? String.format("%.1f%%", data.getBloodOxygenLevel()) : "нет данных"
        );
    }
}