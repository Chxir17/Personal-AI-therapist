package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.domain.model.entities.*;
import com.aitherapist.aitherapist.services.DoctorServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class HistoryPatients implements ICommand {
    @Autowired
    DoctorServiceImpl doctorService;

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long doctorId = extractUserId(update);
        Long chatId = getChatId(update);

        if (doctorId == null) {
            return new SendMessage(chatId.toString(), "❌ Ошибка: не удалось определить ваш профиль врача");
        }

        System.out.println("doctorId - " + doctorId);
        List<Patient> patients = doctorService.getPatients(doctorId);

        if (patients.isEmpty()) {
            return new SendMessage(chatId.toString(), "У вас пока нет пациентов.");
        }

        StringBuilder message = new StringBuilder("📋 Список ваших пациентов:\n\n");

        for (Patient patient : patients) {
            message.append(getPatientInfo(patient)).append("\n\n");
            message.append(getHealthDataInfo(patient)).append("\n");
            message.append("────────────────────\n");
        }

        SendMessage response = new SendMessage(chatId.toString(), message.toString());
        response.enableHtml(true);
        return response;
    }

    private String getPatientInfo(Patient patient) {
        return String.format(
                "<b>👤 Пациент:</b> %s\n" +
                        "<b>📞 Телефон:</b> %s\n" +
                        "<b>🎂 Возраст:</b> %d\n" +
                        "<b>🚻 Пол:</b> %s",
                patient.getName(),
                patient.getPhoneNumber() != null ? patient.getPhoneNumber() : "не указан",
                patient.getAge() != null ? patient.getAge() : 0,
                patient.getGender() != null ? (patient.getGender() ? "Мужской" : "Женский") : "не указан"
        );
    }

    private String getHealthDataInfo(Patient patient) {
        List<DailyHealthData> dailyHealthDataList = patient.getDailyHealthDataList();
        InitialHealthData initHealthData = patient.getInitialData();
        if (dailyHealthDataList.isEmpty()) {
            return "<i>Медицинские данные отсутствуют</i>";
        }

        StringBuilder healthInfo = new StringBuilder("<b>📊 Медицинские показатели:</b>\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (DailyHealthData data : dailyHealthDataList) {
            healthInfo.append(String.format(
                            "🫀 <b>Пульс:</b> %d\n" +
                            "💊 <b>Давление:</b> %s\n" +
                            "🌡 <b>Температура:</b> %.1f\n" +
                            "💤 <b>Сон:</b> %.1f часов",
                    data.getPulse() != null ? data.getPulse() : 0,
                    data.getPressure() != null ? data.getPressure() : "не измерялось",
                    data.getTemperature() != null ? data.getTemperature() : 0,
                    data.getHoursOfSleepToday() != null ? data.getHoursOfSleepToday() : 0
            ));

            if (initHealthData.getChronicDiseases() != null && !initHealthData.getChronicDiseases().isEmpty()) {
                healthInfo.append("\n<b>Хронические заболевания:</b> ").append(initHealthData.getChronicDiseases());
            }
        }

        return healthInfo.toString();
    }

    private Long extractUserId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        }
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }
        return null;
    }

    private Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        }
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        return null;
    }
}