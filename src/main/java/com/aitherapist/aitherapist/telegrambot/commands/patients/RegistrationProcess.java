package com.aitherapist.aitherapist.telegrambot.commands.patients;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.model.PatientRegistrationDto;
import com.aitherapist.aitherapist.domain.model.entities.*;
import com.aitherapist.aitherapist.interactionWithGigaApi.inputParser.ParseUserPrompt;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.model.ClientRegistrationState;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.time.format.DateTimeFormatter;

@Component
public class RegistrationProcess {
    private final Verification verification;
    private final ParseUserPrompt parseUserPrompt;
    private final ITelegramExecutor telegramExecutor;

    @Autowired
    public RegistrationProcess(Verification verification, ParseUserPrompt parseUserPrompt,
                               @Lazy ITelegramExecutor telegramExecutor) {
        this.verification = verification;
        this.parseUserPrompt = parseUserPrompt;
        this.telegramExecutor = telegramExecutor;
    }

    public SendMessage acceptOrEditMedicalInitData(InitialHealthData initialHealthData, Update update, User patient) {
        String genderDisplay = patient.getGender() == null ? "Не указан" :
                (patient.getGender() ? "♂ Мужской" : "♀ Женский");

        String birthDateAndAge;
        if (patient.getBirthDate() != null) {
            String formattedDate = patient.getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            int age = patient.getAge();
            birthDateAndAge = String.format("%s (%d лет)", formattedDate, age);
        } else {
            birthDateAndAge = "Не указана";
        }

        String chronicDiseasesDisplay = "Нет";
        if (initialHealthData.getChronicDiseases() != null) {
            if (initialHealthData.getChronicDiseases().equalsIgnoreCase("false")) {
                chronicDiseasesDisplay = "Нет";
            } else if (initialHealthData.getChronicDiseases().equalsIgnoreCase("true")) {
                chronicDiseasesDisplay = "Да";
            } else {
                chronicDiseasesDisplay = initialHealthData.getChronicDiseases();
            }
        }

        String badHabitsDisplay = "Нет";
        if (initialHealthData.getBadHabits() != null) {
            if (initialHealthData.getBadHabits().equalsIgnoreCase("false")) {
                badHabitsDisplay = "Нет";
            } else if (!initialHealthData.getBadHabits().equalsIgnoreCase("true")) {
                badHabitsDisplay = initialHealthData.getBadHabits();
            }
        }


        String heightDisplay = initialHealthData.getHeight() != null ?
                String.valueOf(initialHealthData.getHeight()) : "Не указан";

        String weightDisplay = initialHealthData.getWeight() != null ?
                String.valueOf(initialHealthData.getWeight()) : "Не указан";

        String message = String.format("""
📝 *Вы ввели данные:*

👤 *Имя:* %s
🎂 *Дата рождения (возраст):* %s
🚻 *Пол:* %s

🏥 *Хронические заболевания:* %s
📏 *Рост:* %s
⚖️ *Вес:* %s
🚬 *Вредные привычки:* %s
""",
                patient.getName(),
                birthDateAndAge,
                genderDisplay,
                chronicDiseasesDisplay,
                heightDisplay,
                weightDisplay,
                badHabitsDisplay);

        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createAcceptOrEditKeyboardPatient();
        String fullMessage = message + "\n\nВыберите действие:";

        if (update.hasCallbackQuery()) {
            try {
                telegramExecutor.editMessageText(
                        String.valueOf(update.getCallbackQuery().getMessage().getChatId()),
                        update.getCallbackQuery().getMessage().getMessageId(),
                        fullMessage,
                        keyboard
                );
                return null;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        return SendMessage.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text(fullMessage)
                .parseMode("Markdown")
                .replyMarkup(keyboard)
                .build();
    }

    public SendMessage requestPhoneNumber(Long chatId, Update update, ITelegramExecutor telegramExecutor) {
        String messageText = Answers.PLEASE_GIVE_TELEPHONE_NUMBER.getMessage();
        telegramExecutor.deleteMessage(chatId.toString(), update.getCallbackQuery().getMessage().getMessageId());


        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(messageText)
                .replyMarkup(Verification.createContactRequestKeyboard())
                .build();
    }

    private void fillCommonPatientFields(Patient patient, Long userId, PatientRegistrationDto dto, RegistrationContext registrationContext) {
        patient.setPhoneNumber(registrationContext.getTelephone(userId));
        patient.setName(dto.getName());
        patient.setBirthDate(dto.getBirthDate());
        patient.setGender(dto.getGender());
        patient.setTelegramId(userId);

        InitialHealthData healthData = new InitialHealthData();
        healthData.setHeight(dto.getHeight());
        healthData.setWeight(dto.getWeight());
        healthData.setChronicDiseases(dto.getChronicDiseases() != null ? dto.getChronicDiseases() : "false");
        healthData.setBadHabits(dto.getBadHabits());
        healthData.setPatient(patient);

        patient.setInitialData(healthData);
    }
    public SendMessage handleQuestionnaire(Update update, RegistrationContext registrationContext, Long userId, UserServiceImpl userService, ObjectMapper mapper, boolean isClinicPatient) throws TelegramApiException, InterruptedException, JsonProcessingException {
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
                        .text(Answers.CHRONIC_DISEASES_QUESTION.getMessage())
                        .build();
            }

            case 4 -> {
                state.getBase().append("chronicDiseases: ").append(text).append("\n");
                state.setCurrentStep(5);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.HEIGHT_QUESTION.getMessage())
                        .build();
            }
            case 5 -> {
                state.getBase().append("height: ").append(text).append("\n");
                state.setCurrentStep(6);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.WEIGHT_QUESTION.getMessage())
                        .build();
            }
            case 6 -> {
                state.getBase().append("weight: ").append(text).append("\n");
                state.setCurrentStep(7);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.BAD_HABITS_QUESTION.getMessage())
                        .build();
            }
            case 7 -> {
                state.getBase().append("badHabits: ").append(text).append("\n");
                System.out.println("STATE " + state.getBase().toString());
                String response = parseUserPrompt.patientRegistrationParser(state.getBase().toString());
                String jsonWithType;
                if (isClinicPatient) {
                    jsonWithType = "{\"user_type\":\"CLINIC_PATIENT\"," + response.substring(1);
                } else {
                    jsonWithType = "{\"user_type\":\"BOT_PATIENT\"," + response.substring(1);
                }
                state.setCurrentStep(8);


                mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
                PatientRegistrationDto dto = mapper.readValue(jsonWithType, PatientRegistrationDto.class);

                if (dto.getBirthDate() != null) {
                    int age = java.time.Period.between(dto.getBirthDate(), java.time.LocalDate.now()).getYears();
                    if (age < 5 || age > 120) {
                        dto.setBirthDate(null);
                    }
                } else {
                    dto.setBirthDate(null);
                }

                if (dto.getHeight() != null && (dto.getHeight() < 50 || dto.getHeight() > 280)) {
                    dto.setHeight(null);
                }

                if (dto.getWeight() != null && (dto.getWeight() < 15 || dto.getWeight() > 300)) {
                    dto.setWeight(null);
                }

                Patient patient;

                if (isClinicPatient) {
                    ClinicPatient clinicPatient = new ClinicPatient();
                    clinicPatient.setRole(Roles.CLINIC_PATIENT);
                    clinicPatient.setMedicalCardNumber(dto.getMedicalCardNumber());
                    clinicPatient.setClinicId(1L);
                    patient = clinicPatient;
                } else {
                    NonClinicPatient nonClinicPatient = new NonClinicPatient();
                    nonClinicPatient.setRole(Roles.BOT_PATIENT);
                    patient = nonClinicPatient;
                }
                fillCommonPatientFields(patient, userId, dto, registrationContext);
                try {
                    userService.saveUser(patient);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }

                return acceptOrEditMedicalInitData(patient.getInitialData(), update, patient);
            }
            default -> {
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Неизвестный шаг регистрации")
                        .build();
            }
        }
    }
}
