package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;
import com.aitherapist.aitherapist.services.InitialHealthDataServiceImpl;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.interactionWithGigaApi.inputParser.ParseUserPrompt;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.DoctorSendMessageToPatient;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class StartClinicPatient implements ICommand {
    private String telephoneNumber;
    private IMessageSender messageSender;
    private DoctorSendMessageToPatient sendMessageUser;
    private ClinicPatient patient;
    private int currentRegistrationStep = 1;
    private UserServiceImpl userService;
    private StringBuilder userInput = new StringBuilder(); // Используем StringBuilder для эффективности
    private PatientServiceImpl patientService;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Autowired
    InitialHealthDataServiceImpl initialHealthDataService;

    @Autowired
    public Verification verification;

    @Autowired
    public StartClinicPatient(TelegramMessageSender messageSender, DoctorSendMessageToPatient sendMessageUser) {
        this.messageSender = messageSender;
        this.sendMessageUser = sendMessageUser;
    }

    private SendMessage acceptOrEditMedicalInitData(InitialHealthData dailyHealthData, Update update) {
        String genderDisplay = patient.getGender() ? "♂ Мужской" : "♀ Женский";

        String message = String.format("""
        📝 *Вы ввели данные:*
        
        👤 *Имя:* %s
        🎂 *Дата рождения:* %s
        🚻 *Пол:* %s
        
        💓 *Аритмия:* %s
        🏥 *Хронические заболевания:* %s
        📏 *Рост:* %s
        ⚖️ *Вес:* %s
        🚬 *Вредные привычки:* %s
        """,
                patient.getName(),
                patient.getAge(),
                genderDisplay,
                dailyHealthData.getArrhythmia(),
                dailyHealthData.getChronicDiseases(),
                dailyHealthData.getHeight(),
                dailyHealthData.getWeight(),
                dailyHealthData.getBadHabits());

        return SendMessage.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text(message + "\n\nВыберите действие:")
                .parseMode("Markdown")
                .replyMarkup(InlineKeyboardFactory.createAcceptOrEditKeyboard())
                .build();
    }

    private SendMessage handleQuestionnaire(Update update) throws TelegramApiException, InterruptedException, JsonProcessingException {
        Long chatId = TelegramIdUtils.getChatId(update);

        if (!update.hasMessage()) {
            if (currentRegistrationStep == 1) {
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.GIVE_NAME.getMessage())
                        .build();
            }
            return null;
        }
        String text = update.getMessage().getText();
        Long userId = update.getMessage().getFrom().getId();

        switch (currentRegistrationStep) {
            case 1 -> {
                userInput.append("name: ").append(text).append("\n");
                currentRegistrationStep++;
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.AGE.getMessage())
                        .build();
            }
            case 2 -> {
                userInput.append("age: ").append(text).append("\n");
                currentRegistrationStep++;
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.GENDER.getMessage())
                        .build();
            }
            case 3 -> {
                userInput.append("gender: ").append(text).append("\n");
                currentRegistrationStep++;
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Есть ли у вас аритмия?")
                        .build();
            }
            case 4 -> {
                userInput.append("arrhythmia: ").append(text).append("\n");
                currentRegistrationStep++;
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Есть ли у вас хронические заболевания?")
                        .build();
            }
            case 5 -> {
                userInput.append("chronicDiseases: ").append(text).append("\n");
                currentRegistrationStep++;
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Введите ваш рост (в сантиметрах):")
                        .build();
            }
            case 6 -> {
                userInput.append("height: ").append(text).append("\n");
                currentRegistrationStep++;
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Введите ваш вес (в килограммах):")
                        .build();
            }
            case 7 -> {
                userInput.append("weight: ").append(text).append("\n");
                currentRegistrationStep++;
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Есть ли у вас вредные привычки?")
                        .build();
            }
            case 8 -> {
                userInput.append("badHabits: ").append(text).append("\n");
                String response = ParseUserPrompt.patientRegistrationParser(userInput.toString());
                String jsonWithType = "{\"user_type\":\"CLINIC_PATIENT\"," + response.substring(1);

                InitialHealthData initialHealthData = mapper.readValue(jsonWithType, InitialHealthData.class);
                initialHealthDataService.putInitialHealthDataByUserId(initialHealthData, userId);
                patient = mapper.readValue(jsonWithType, ClinicPatient.class);
                patient.setInitialData(initialHealthData);
                userService.saveUser(patient);
                initialHealthDataService.putInitialHealthDataByUserId(initialHealthData, userId);


                currentRegistrationStep = 0;
                userInput.setLength(0);

                return acceptOrEditMedicalInitData(initialHealthData, update);
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
        Long userId = TelegramIdUtils.extractUserId(update);
        registrationContext.setStatus(userId, Status.REGISTRATION_CLINIC_PATIENT);
        if (userId == null) {
            return SendMessage.builder()
                    .chatId(TelegramIdUtils.getChatId(update).toString())
                    .text("Не удалось определить пользователя")
                    .build();
        }

        if (registrationContext.getStatus(userId) == Status.REGISTRATION_CLINIC_PATIENT) {
            try {
                return handleQuestionnaire(update);
            } catch (Exception e) {
                return SendMessage.builder()
                        .chatId(TelegramIdUtils.getChatId(update).toString())
                        .text("Ошибка обработки данных")
                        .build();
            }
        }

        if (!registrationContext.isVerify(userId)) {
            return requestPhoneNumber(TelegramIdUtils.getChatId(update));
        }
        return SendMessage.builder()
                .chatId(TelegramIdUtils.getChatId(update).toString())
                .text("Вы уже верифицированы. Выберите действие:")
                .replyMarkup(InlineKeyboardFactory.createPatientDefaultKeyboard())
                .build();
    }
    private SendMessage requestPhoneNumber(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(Answers.PLEASE_GIVE_TELEPHONE_NUMBER.getMessage())
                .replyMarkup(verification.createContactRequestKeyboard())
                .build();
    }
}
