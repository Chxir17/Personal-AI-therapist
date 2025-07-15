package com.aitherapist.aitherapist.telegrambot.commands.clinicPatient;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.interactionWithGigaApi.ParseUserPrompt;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.SendMessageUser;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.Status;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
public class StartClinicPatient implements ICommand {
    private String telephoneNumber;
    private IMessageSender messageSender;
    private SendMessageUser sendMessageUser;
    private Patient patient;
    private int currentRegistrationStep = 1;
    private StringBuilder userInput = new StringBuilder();
    private PatientServiceImpl patientService;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


    @Autowired
    public Verification verification;

    @Autowired
    public StartClinicPatient(TelegramMessageSender messageSender, SendMessageUser sendMessageUser) {
        this.messageSender = messageSender;
        this.sendMessageUser = sendMessageUser;
    }

    private void acceptOrEditMedicalInitData(InitialHealthData dailyHealthData, Update update) throws TelegramApiException {
        Map<String, String> buttons = new HashMap<>();
        String message = "Вы ввели:\n Аритмия - " + dailyHealthData.getArrhythmia() + "\n Хронические заболевания - " + dailyHealthData.getChronicDiseases() + "\n Вес - "
                + dailyHealthData.getHeight() + "\n Вес - " + dailyHealthData.getWeight() + "\n Вредные привычки - " + dailyHealthData.getBadHabits();
        messageSender.sendMessage(update.getMessage().getChatId(), message);
        buttons.put("Принять", "/acceptMedicalData");
        buttons.put("Изменить параметры", "/editMedicalData");

        InlineKeyboardMarkup replyKeyboardDoctor = InlineKeyboardFactory.createInlineKeyboard(buttons, 2);

        messageSender.sendMessage(SendMessage.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text("Выберите команду")
                .replyMarkup(replyKeyboardDoctor)
                .build());
    }

    private SendMessage handleQuestionnaire(Update update) throws TelegramApiException, InterruptedException, JsonProcessingException {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        if (!update.hasMessage()) {
            if (currentRegistrationStep == 1) {
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.GIVE_NAME.getMessage())
                        .build();
            }
            return null;
        }
        Long userId = update.getMessage().getFrom().getId();

        switch (currentRegistrationStep) {
            case 1 -> {
                userInput.append("name: ").append(text).append("\n");
                currentRegistrationStep++;
                messageSender.sendMessage(chatId, Answers.AGE.getMessage());
            }
            case 2 -> {
                userInput.append("age: ").append(text).append("\n");
                currentRegistrationStep++;
                messageSender.sendMessage(chatId, Answers.GENDER.getMessage());
            }
            case 3 -> {
                userInput.append("gender: ").append(text).append("\n");
                currentRegistrationStep++;
                messageSender.sendMessage(chatId, "Есть ли у вас аритмия?");
            }
            case 4 -> {
                userInput.append("arrhythmia: ").append(text).append("\n");
                currentRegistrationStep++;
                messageSender.sendMessage(chatId, "Есть ли у вас хронические заболевания?");
            }
            case 5 -> {
                userInput.append("chronicDiseases: ").append(text).append("\n");
                currentRegistrationStep++;
                messageSender.sendMessage(chatId, "Введите ваш рост (в сантиметрах):");
            }
            case 6 -> {
                userInput.append("height: ").append(text).append("\n");
                currentRegistrationStep++;
                messageSender.sendMessage(chatId, "Введите ваш вес (в килограммах):");
            }
            case 7 -> {
                userInput.append("weight: ").append(text).append("\n");
                currentRegistrationStep++;
                messageSender.sendMessage(chatId, "Есть ли у вас вредные привычки?");
            }
            case 8 -> {
                userInput.append("badHabits: ").append(text).append("\n");
                String response = ParseUserPrompt.patientRegistrationParser(userInput.toString());
                String jsonWithType = "{\"user_type\":\"CLINIC_PATIENT\"," + response.substring(1);

                InitialHealthData healthData = mapper.readValue(jsonWithType, InitialHealthData.class);
                patient = mapper.readValue(jsonWithType, Patient.class);
                patient.setInitialData(healthData);
                acceptOrEditMedicalInitData(healthData, update);
                currentRegistrationStep = 0;
                userInput.setLength(0); // Очищаем буфер после завершения регистрации
            }
            default -> {
                messageSender.sendMessage(chatId, "Неизвестный шаг регистрации");
                currentRegistrationStep = 0;
                userInput.setLength(0);
            }
        }
        return null;
    }

    /**
     * verify user. If user hide telephone number-> create buttom and request.
     * @param update
     * @return
     * @throws TelegramApiException
     */
    /**
     * g
     * Get telephone number and set to field
     *
     * @param update
     * @return
     */
    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        registrationContext.setStatus(update.getMessage().getFrom().getId(), Status.SECOND_PART_REGISTRATION);

//        boolean verStatus = verify(update);
//        while(!verStatus){                //FIXME что с этим делать как это исправить? какой метод тут должен быть теперь?
//            verStatus = verify(update);
//        }
        long chatId = update.getMessage().getChatId();
        return new SendMessage(String.valueOf(chatId), Answers.WRITE_MEDICAL_INFO.getMessage());
    }
}
