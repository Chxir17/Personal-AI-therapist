package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.domain.model.entities.*;
import com.aitherapist.aitherapist.services.DoctorServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long doctorId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);

        if (doctorId == null) {
            return new SendMessage(chatId.toString(), "❌ Ошибка: не удалось определить ваш профиль врача");
        }

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

        StringBuilder healthInfo = new StringBuilder("<b>📊 Медицинские данные:</b>\n");

        if (initHealthData != null) {
            if (initHealthData.getChronicDiseases() != null && !initHealthData.getChronicDiseases().isEmpty()) {
                healthInfo.append("🩺 <b>Хронические заболевания:</b> ")
                        .append(initHealthData.getChronicDiseases()).append("\n");
            }
            if (initHealthData.getHeight() != null) {
                healthInfo.append("📏 <b>Рост:</b> ").append(initHealthData.getHeight()).append(" см\n");
            }
            if (initHealthData.getWeight() != null) {
                healthInfo.append("⚖️ <b>Вес:</b> ").append(initHealthData.getWeight()).append(" кг\n");
            }
            if (initHealthData.getBadHabits() != null && !initHealthData.getBadHabits().isEmpty()) {
                healthInfo.append("🚬 <b>Вредные привычки:</b> ").append(initHealthData.getBadHabits()).append("\n");
            }
        }

        if (dailyHealthDataList != null && !dailyHealthDataList.isEmpty()) {
            for (DailyHealthData data : dailyHealthDataList) {
                healthInfo.append("\n<b>🗓️ Измерения:</b>\n")
                        .append(String.format(
                                "🫀 <b>Пульс:</b> %d\n" +
                                        "💊 <b>Давление:</b> %s\n" +
                                        "🌡 <b>Температура:</b> %.1f\n" +
                                        "💤 <b>Сон:</b> %.1f часов\n",
                                data.getPulse() != null ? data.getPulse() : 0,
                                data.getPressure() != null ? data.getPressure() : "не измерялось",
                                data.getTemperature() != null ? data.getTemperature() : 0,
                                data.getHoursOfSleepToday() != null ? data.getHoursOfSleepToday() : 0
                        ));
            }
        }

        if (healthInfo.toString().equals("<b>📊 Медицинские данные:</b>\n")) {
            return "<i>Медицинские данные отсутствуют</i>";
        }

        return healthInfo.toString();
    }



}