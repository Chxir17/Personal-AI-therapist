package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.domain.model.entities.*;
import com.aitherapist.aitherapist.services.DoctorServiceImpl;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class GetLastPatientMedicalData implements ICommand {
    private final DoctorServiceImpl doctorService;

    @Autowired
    public GetLastPatientMedicalData(DoctorServiceImpl doctorService){
        this.doctorService = doctorService;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        long chatId = TelegramIdUtils.getChatId(update);
        long doctorId = update.getMessage().getFrom().getId();

        if (registrationContext.getStatus(doctorId) == Status.GIVING_PATIENT_ID) {
            try {
                Long patientId = Long.parseLong(update.getMessage().getText());
                return sendLastPatientData(chatId, doctorId, patientId, registrationContext);
            } catch (NumberFormatException e) {
                return new SendMessage(String.valueOf(chatId), "❌ Неверный формат ID. Пожалуйста, введите числовой ID пациента.");
            }
        } else {
            return showPatientList(chatId, doctorId, registrationContext);
        }
    }

    private SendMessage showPatientList(long chatId, long doctorId, RegistrationContext registrationContext) {
        List<Patient> patients = doctorService.getPatients(doctorId);

        if (patients.isEmpty()) {
            return new SendMessage(String.valueOf(chatId), "У вас пока нет пациентов.");
        }

        StringBuilder userMessage = new StringBuilder("📋 Ваши пациенты:\n\n<b>Имя                     ID</b>\n");

        for (int i = 0; i < patients.size(); i++) {
            userMessage.append(i + 1)
                    .append(". ")
                    .append(patients.get(i).getName())
                    .append(" - ")
                    .append(patients.get(i).getId())
                    .append("\n");
        }

        userMessage.append("\nВведите <b>ID пациента</b>, чтобы получить его последние измерения");
        registrationContext.setStatus(doctorId, Status.GIVING_PATIENT_ID);

        SendMessage response = new SendMessage(String.valueOf(chatId), userMessage.toString());
        response.enableHtml(true);
        return response;
    }

    private SendMessage sendLastPatientData(long chatId, long doctorId, Long patientId, RegistrationContext registrationContext) {
        Optional<Patient> patientOpt = doctorService.getPatients(doctorId).stream()
                .filter(p -> p.getId().equals(patientId))
                .findFirst();

        if (patientOpt.isEmpty()) {
            registrationContext.setStatus(doctorId, Status.NONE);
            return new SendMessage(String.valueOf(chatId), "❌ Пациент с ID " + patientId + " не найден среди ваших пациентов.");
        }

        Patient patient = patientOpt.get();
        List<DailyHealthData> healthData = patient.getDailyHealthDataList();

        if (healthData == null || healthData.isEmpty()) {
            registrationContext.setStatus(doctorId, Status.NONE);
            return new SendMessage(String.valueOf(chatId), "ℹ️ У пациента " + patient.getName() + " нет данных измерений.");
        }

        DailyHealthData lastData = healthData.stream()
                .max(Comparator.comparing(DailyHealthData::getId))
                .orElseThrow();

        String message = String.format(
                "📊 <b>Последние измерения пациента %s</b>\n\n" +
                        "🆔 <b>ID измерения:</b> %d\n" +
                        "🫀 <b>Пульс:</b> %d\n" +
                        "💊 <b>Давление:</b> %s\n" +
                        "🌡 <b>Температура:</b> %.1f\n" +
                        "💤 <b>Сон:</b> %.1f часов\n" +
                patient.getName(),
                lastData.getId(),
                lastData.getPulse() != null ? lastData.getPulse() : 0,
                lastData.getPressure() != null ? lastData.getPressure() : "не измерялось",
                lastData.getTemperature() != null ? lastData.getTemperature() : 0,
                lastData.getHoursOfSleepToday() != null ? lastData.getHoursOfSleepToday() : 0
        );

        SendMessage response = new SendMessage(String.valueOf(chatId), message);
        response.enableHtml(true);
        return response;
    }
}