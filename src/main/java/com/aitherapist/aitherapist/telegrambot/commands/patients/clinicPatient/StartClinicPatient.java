package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.model.PatientRegistrationDto;
import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.interactionWithGigaApi.inputParser.ParseUserPrompt;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.DoctorSendMessageToPatient;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.ClientRegistrationState;
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

import java.time.format.DateTimeFormatter;

@Component
public class StartClinicPatient implements ICommand {
    private final RegistrationContext registrationContext;
    private IMessageSender messageSender;
    private DoctorSendMessageToPatient sendMessageUser;
    private UserServiceImpl userService;
    private final ParseUserPrompt parseUserPrompt;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public Verification verification;
    public PatientServiceImpl patientService;

    @Autowired
    public StartClinicPatient(
            TelegramMessageSender messageSender,
            DoctorSendMessageToPatient sendMessageUser,
            PatientServiceImpl patientService,
            UserServiceImpl userService,
            ParseUserPrompt parseUserPrompt,
            RegistrationContext registrationContext) {
        this.messageSender = messageSender;
        this.sendMessageUser = sendMessageUser;
        this.patientService = patientService;
        this.userService = userService;
        this.registrationContext = registrationContext;
        this.parseUserPrompt = parseUserPrompt;
    }

    private SendMessage acceptOrEditMedicalInitData(InitialHealthData initialHealthData, Update update, ClinicPatient clinicPatient) {
        String genderDisplay = clinicPatient.getGender() ? "♂ Мужской" : "♀ Женский";

        // Format birth date and age information
        String birthDateAndAge;
        if (clinicPatient.getBirthDate() != null) {
            String formattedDate = clinicPatient.getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            int age = clinicPatient.getAge();
            birthDateAndAge = String.format("%s (%d лет)", formattedDate, age);
        } else {
            birthDateAndAge = "Не указана";
        }

        String message = String.format("""
        📝 *Вы ввели данные:*
        
        👤 *Имя:* %s
        🎂 *Дата рождения (возраст):* %s
        🚻 *Пол:* %s
        
        💓 *Аритмия:* %s
        🏥 *Хронические заболевания:* %s
        📏 *Рост:* %s
        ⚖️ *Вес:* %s
        🚬 *Вредные привычки:* %s
        """,
                clinicPatient.getName(),
                birthDateAndAge,
                genderDisplay,
                initialHealthData.getArrhythmia() ? "Да" : "Нет",
                initialHealthData.getChronicDiseases().equalsIgnoreCase("false") ? "Нет" : (initialHealthData.getChronicDiseases().equalsIgnoreCase("true") ? "Да" : initialHealthData.getChronicDiseases()),
                initialHealthData.getHeight(),
                initialHealthData.getWeight(),
                initialHealthData.getBadHabits().equalsIgnoreCase("true") ? "Да" : initialHealthData.getBadHabits());

        return SendMessage.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text(message + "\n\nВыберите действие:")
                .parseMode("Markdown")
                .replyMarkup(InlineKeyboardFactory.createAcceptOrEditKeyboardClinic())
                .build();
    }

    private SendMessage handleQuestionnaire(Update update, RegistrationContext registrationContext, Long userId) throws TelegramApiException, InterruptedException, JsonProcessingException {
        Long chatId = TelegramIdUtils.getChatId(update);
        ClientRegistrationState state = registrationContext.getClientRegistrationState(chatId);
        if (update.getMessage().hasContact()) {
            registrationContext.setTelephone(userId, update.getMessage().getContact().getPhoneNumber());
            return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.GIVE_NAME.getMessage())
                        .build();
        }
        String text = update.getMessage().getText();
        switch (state.getCurrentStep()) {
            case 1 -> {
                state.getBase().append("name: ").append(text).append("\n");
                state.setCurrentStep(2);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.AGE.getMessage())
                        .build();
            }
            case 2 -> {
                state.getBase().append("age: ").append(text).append("\n");
                state.setCurrentStep(3);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.GENDER.getMessage())
                        .build();
            }
            case 3 -> {
                state.getBase().append("gender: ").append(text).append("\n");
                state.setCurrentStep(4);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.ARHYTHMIA_QUESTION.getMessage())
                        .build();
            }
            case 4 -> {
                state.getBase().append("arrhythmia: ").append(text).append("\n");
                state.setCurrentStep(5);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.CHRONIC_DISEASES_QUESTION.getMessage())
                        .build();
            }
            case 5 -> {
                state.getBase().append("chronicDiseases: ").append(text).append("\n");
                state.setCurrentStep(6);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.HEIGHT_QUESTION.getMessage())
                        .build();
            }
            case 6 -> {
                state.getBase().append("height: ").append(text).append("\n");
                state.setCurrentStep(7);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.WEIGHT_QUESTION.getMessage())
                        .build();
            }
            case 7 -> {
                state.getBase().append("weight: ").append(text).append("\n");
                state.setCurrentStep(8);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.BAD_HABITS_QUESTION.getMessage())
                        .build();
            }
            case 8 -> {
                state.getBase().append("badHabits: ").append(text).append("\n");
                String response = parseUserPrompt.patientRegistrationParser(state.getBase().toString() );
                String jsonWithType = "{\"user_type\":\"CLINIC_PATIENT\"," + response.substring(1);
                PatientRegistrationDto dto = mapper.readValue(jsonWithType, PatientRegistrationDto.class);
                ClinicPatient patient = new ClinicPatient();
                patient.setPhoneNumber(registrationContext.getTelephone(userId));
                patient.setName(dto.getName());
                patient.setBirthDate(dto.getBirthDate());
                patient.setGender(dto.getGender());
                patient.setRole(Roles.CLINIC_PATIENT);
                patient.setClinicId(dto.getClinicId());
                patient.setMedicalCardNumber(dto.getMedicalCardNumber());
                InitialHealthData healthData = new InitialHealthData();
                healthData.setArrhythmia(dto.getArrhythmia());
                healthData.setHeight(dto.getHeight());
                healthData.setWeight(dto.getWeight());
                healthData.setChronicDiseases(dto.getChronicDiseases());
                healthData.setBadHabits(dto.getBadHabits());
                healthData.setPatient(patient);
                patient.setInitialData(healthData);
                try {
                    patient.setTelegramId(userId);
                    patient.setClinicId(1L);
                    userService.saveUser(patient);
                } catch (Exception e) {
                    System.err.println("Ошибка сохранения: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
                registrationContext.clearClientRegistrationState(userId);
                return acceptOrEditMedicalInitData(healthData, update, patient);
            }
            default -> {
                registrationContext.clearClientRegistrationState(userId);
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
        if (userId == null) {
            return SendMessage.builder()
                    .chatId(TelegramIdUtils.getChatId(update).toString())
                    .text("Не удалось определить пользователя")
                    .build();
        }
        if (registrationContext.getStatus(userId) == Status.REGISTERED_CLINIC_PATIENT) {
            try {
                return handleQuestionnaire(update, registrationContext, userId);
            } catch (Exception e) {
                return SendMessage.builder()
                        .chatId(TelegramIdUtils.getChatId(update).toString())
                        .text("Ошибка обработки данных")
                        .build();
            }
        } else {
            if (registrationContext.isVerify(userId)) {
                registrationContext.setStatus(userId, Status.REGISTRATION_CLINIC_PATIENT);
                return requestPhoneNumber(TelegramIdUtils.getChatId(update));
            }
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
