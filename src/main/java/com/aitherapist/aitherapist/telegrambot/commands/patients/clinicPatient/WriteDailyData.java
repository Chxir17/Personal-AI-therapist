package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;
import com.aitherapist.aitherapist.interactionWithGigaApi.MakeMedicalRecommendation;
import com.aitherapist.aitherapist.interactionWithGigaApi.ParseUserPrompt;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.aitherapist.aitherapist.interactionWithGigaApi.MakeMedicalRecommendation.giveMedicalRecommendationBeta;
import static com.aitherapist.aitherapist.interactionWithGigaApi.MakeMedicalRecommendation.giveMedicalRecommendationWithScoreBeta;

@Component
public class WriteDailyData implements ICommand {
    private int currentRegistrationStep = 1;
    private StringBuilder userInput = new StringBuilder();

    private SendMessage handleQuestionnaire(Update update) throws TelegramApiException {
        Long chatId = TelegramIdUtils.getChatId(update);


        switch (currentRegistrationStep) {
            case 1 -> {
                currentRegistrationStep++;
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Введите температуру тела (°C):")
                        .build();
            }
            case 2 -> {

                currentRegistrationStep++;
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Сколько часов вы спали сегодня?")
                        .build();
            }
            case 3 -> {
                currentRegistrationStep++;
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Введите ваш пульс (уд/мин):")
                        .build();
            }
            case 4 -> {
                currentRegistrationStep++;
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Введите ваше давление (формат: 120/80):")
                        .build();
            }
            case 5 -> {
                InitialHealthData initialData = new InitialHealthData();
                initialData.setHeartPain(true);
                initialData.setArrhythmia(true);
                initialData.setChronicDiseases(null);
                initialData.setHeight(175.5);
                initialData.setWeight(80.0);
                initialData.setBadHabits("Smoking");


                // Создаем ClinicPatient
                ClinicPatient patient = new ClinicPatient();
                patient.setClinicId(1L);
                patient.setMedicalCardNumber("192.168.20.2");
                patient.setInitialData(initialData);

                patient.setName("Джон Смит");
                patient.setBirthDate(LocalDate.of(1990, 5, 20));
                patient.setGender(true);
                patient.setPhoneNumber("+123456789");
                // Создаем DailyHealthData
                DailyHealthData dailyData = new DailyHealthData();
                dailyData.setBloodOxygenLevel(98.5);
                dailyData.setTemperature(36.7);
                dailyData.setHoursOfSleepToday(7.5);
                dailyData.setPulse(90L);
                dailyData.setPressure("190/150");
                dailyData.setPatient(patient);

                List<DailyHealthData> dailyList = new ArrayList<>();
                dailyList.add(dailyData);

                patient.setDailyHealthDataList(dailyList);
                String response4 = giveMedicalRecommendationWithScoreBeta(patient);
                System.out.println("---------" + response4 + "-----------");
                currentRegistrationStep = 0;
                userInput.setLength(0);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(response4)
                        .build();
            }
            default -> {
                currentRegistrationStep = 0;
                userInput.setLength(0);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Неизвестный шаг регистрации")
                        .build();
            }
        }
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        return handleQuestionnaire(update);
    }
}