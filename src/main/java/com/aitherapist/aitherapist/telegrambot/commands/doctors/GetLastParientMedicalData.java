package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.domain.model.entities.*;
import com.aitherapist.aitherapist.services.DoctorServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
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
    private IMessageSender messageSender;
    private DoctorServiceImpl doctorService;
    @Autowired
    public void StartClinicPatient(TelegramMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    private static <T> String makeDataList(List<T> values){
        StringBuilder sb = new StringBuilder();
        for (T val : values) {
            sb.append(val).append(", ");
        }
        if (!values.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    public static Map<String, String> buildMedicalHistory(Patient patient) {
        var result = new LinkedHashMap<String, String>();

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

        result.put("sugar", makeDataList(
                history.stream().map(HealthData::getSugar).toList()
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
    public SendMessage apply(Update update) throws TelegramApiException {
        long chatId = update.getMessage().getChatId();
        long doctorId = update.getMessage().getFrom().getId();
        Doctor doctor =  doctorService.getDoctor(doctorId);
        List<Patient> patients = doctorService.getPatients(doctorId);
        String userMessage = "Ваши пациенты:\n имя                        id";
        for(int i = 0; i<patients.size();i++){
            userMessage = userMessage + i+ ". " + patients.get(i).getName() + " - " +  patients.get(i).getId()+"\n";
        }
        userMessage = userMessage + "Введите id пациента чтобы получить его послдение измерения";
        messageSender.sendMessage(chatId, userMessage);
        int counter = 0; //FIXME мусор
        while(!update.hasMessage() && !update.getMessage().hasText()) {
            counter++;
            //FIXME хз дождаться прихода сообщения иначе
        }
        String response = update.getMessage().getText();
        long userId = Integer.parseInt(response);
        Patient patient = doctorService.getPatientById(doctorId, userId);
        while(patient == null){
            messageSender.sendMessage(chatId, "Неверный id пользователя.\n Введите id пациента чтобы получить его послдение измерения");
            while(!update.hasMessage() && !update.getMessage().hasText()) {
                counter++;
                //FIXME хз дождаться прихода сообщения иначе
            }
            response = update.getMessage().getText();
            userId = Integer.parseInt(response);
            patient = doctorService.getPatientById(doctorId, userId);
        }
        String healthData = "";
        Map<String, String> healthDataHistory = buildMedicalHistory(patient);
        healthData += healthDataHistory.toString();
        messageSender.sendMessage(chatId, healthData);
        Map<String, String> buttons = new HashMap<>();
        buttons.put("Получить последние измерения пациента", "/getLastRecords");
        buttons.put("Отправить сообщение пациенту","/sendMessageToPatient");
        buttons.put("Настройки","/settings");
        InlineKeyboardMarkup commands = InlineKeyboardFactory.createInlineKeyboard(buttons, 2);
        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Выберите Комманду")
                .replyMarkup(commands)
                .build();
    }
}
