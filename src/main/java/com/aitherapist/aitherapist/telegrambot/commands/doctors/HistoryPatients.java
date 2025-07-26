package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.domain.model.entities.*;
import com.aitherapist.aitherapist.functionality.QAChatBot.UserQuestions;
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

import java.util.List;

@Component
public class HistoryPatients implements ICommand {
    DoctorServiceImpl doctorService;

    @Autowired
    public HistoryPatients(DoctorServiceImpl doctorService) {
        this.doctorService = doctorService;
    }

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
                patient.getPhoneNumber() != null ? "+" + patient.getPhoneNumber() : "не указан",
                patient.getAge() != null ? patient.getAge() : 0,
                patient.getGender() != null ? (patient.getGender() ? "Мужской" : "Женский") : "не указан"
        );
    }

    private String getHealthDataInfo(Patient patient) {
        InitialHealthData initHealthData = patient.getInitialData();

        StringBuilder healthInfo = new StringBuilder("<b>📊 Медицинские данные:</b>\n");

        if (initHealthData != null) {
            if (initHealthData.getChronicDiseases() != null) {
                String chronicDiseases = "false".equals(initHealthData.getChronicDiseases())
                        ? "нет"
                        : initHealthData.getChronicDiseases();
                if (!chronicDiseases.isEmpty()) {
                    healthInfo.append("🩺 <b>Хронические заболевания:</b> ")
                            .append(chronicDiseases).append("\n");
                }
            }
            if (initHealthData.getHeight() != null) {
                healthInfo.append("📏 <b>Рост:</b> ").append(initHealthData.getHeight()).append(" см\n");
            }
            if (initHealthData.getWeight() != null) {
                healthInfo.append("⚖️ <b>Вес:</b> ").append(initHealthData.getWeight()).append(" кг\n");
            }
            if (initHealthData.getBadHabits() != null) {
                String badHabits = "false".equals(initHealthData.getBadHabits())
                        ? "нет"
                        : initHealthData.getBadHabits();
                if (!badHabits.isEmpty()) {
                    healthInfo.append("🚬 <b>Вредные привычки:</b> ").append(badHabits).append("\n");
                }
            }
        }
        return healthInfo.toString();
    }



}