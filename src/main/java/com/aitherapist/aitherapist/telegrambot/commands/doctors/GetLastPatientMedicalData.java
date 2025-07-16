package com.aitherapist.aitherapist.telegrambot.commands.doctors;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.domain.model.entities.*;
import com.aitherapist.aitherapist.services.DoctorServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class GetLastPatientMedicalData implements ICommand {

    @Autowired
    private DoctorServiceImpl doctorService;

    private Map<String, List<String>> makeDataList(List<?> data) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        // Реализация формирования данных
        return result;
    }

//    private Map<String, List<String>> getPatientHealthData(Patient patient) {
//        Map<String, List<String>> result = new LinkedHashMap<>();
//        List<dailyHealthData> history = patient.getDailyHealthDataList();
//
//        result.put("bloodOxygenLevel", (List<String>) makeDataList(
//                history.stream().map(dailyHealthData::getBloodOxygenLevel).toList()
//        ));
//
//        result.put("temperature", makeDataList(
//                history.stream().map(dailyHealthData::getTemperature).toList()
//        ));
//
//        result.put("hoursOfSleepToday", makeDataList(
//                history.stream().map(dailyHealthData::getHoursOfSleepToday).toList()
//        ));
//
//        result.put("pulse", makeDataList(
//                history.stream().map(dailyHealthData::getPulse).toList()
//        ));
//
//        result.put("pressure", makeDataList(
//                history.stream().map(dailyHealthData::getPressure).toList()
//        ));
//
//        result.put("heartPain", makeDataList(
//                history.stream().map(dailyHealthData::getHeartPain).toList()
//        ));
//
//        result.put("arrhythmia", makeDataList(
//                history.stream().map(dailyHealthData::getArrhythmia).toList()
//        ));
//
//        return result;
//    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        long chatId = TelegramIdUtils.getChatId(update);
        long doctorId = update.getMessage().getFrom().getId();
        Doctor doctor = doctorService.getDoctor(doctorId);
        List<Patient> patients = doctorService.getPatients(doctorId);

        StringBuilder userMessage = new StringBuilder("Ваши пациенты:\nИмя                        ID\n");

        for (int i = 0; i < patients.size(); i++) {
            userMessage.append(i + 1)
                    .append(". ")
                    .append(patients.get(i).getName())
                    .append(" - ")
                    .append(patients.get(i).getId())
                    .append("\n");
        }

        userMessage.append("\nВведите ID пациента, чтобы получить его последние измерения");
        registrationContext.setStatus(doctorId, Status.GIVING_PATIENT_ID);

        return new SendMessage(String.valueOf(chatId), userMessage.toString());
    }
}