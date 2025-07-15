package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.domain.model.entities.*;
import com.aitherapist.aitherapist.services.DoctorServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.Status;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class GetLastParientMedicalData implements ICommand {
    private DoctorServiceImpl doctorService;
    @Autowired



        List<HealthData> history = patient.getHealthDataList();

        result.put("bloodOxygenLevel", makeDataList(
                history.stream().map(HealthData::getBloodOxygenLevel).toList()
        ));

        result.put("temperature", makeDataList(
                history.stream().map(HealthData::getTemperature).toList()
        ));

        result.put("hoursOfSleepToday", makeDataList(
                history.stream().map(HealthData::getHoursOfSleepToday).toList()
        ));

        result.put("pulse", makeDataList(
                history.stream().map(HealthData::getPulse).toList()
        ));

        result.put("pressure", makeDataList(
                history.stream().map(HealthData::getPressure).toList()
        ));

        result.put("heartPain", makeDataList(
                history.stream().map(HealthData::getHeartPain).toList()
        ));

        result.put("arrhythmia", makeDataList(
                history.stream().map(HealthData::getArrhythmia).toList()
        ));

        return result;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        long chatId = update.getMessage().getChatId();
        long doctorId = update.getMessage().getFrom().getId();
        Doctor doctor =  doctorService.getDoctor(doctorId);
        List<Patient> patients = doctorService.getPatients(doctorId);
        StringBuilder userMessage = new StringBuilder("Ваши пациенты:\n имя                        id");
        for(int i = 0; i<patients.size();i++){
            userMessage.append(i).append(". ").append(patients.get(i).getName()).append(" - ").append(patients.get(i).getId()).append("\n");
        }
        userMessage.append("Введите id пациента чтобы получить его послдение измерения");
        registrationContext.setStatus(doctorId, Status.GIVING_PATIENT_ID);
        return new SendMessage(String.valueOf(chatId), userMessage.toString());
    }
}
