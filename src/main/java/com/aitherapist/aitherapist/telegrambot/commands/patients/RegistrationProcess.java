package com.aitherapist.aitherapist.telegrambot.commands.patients;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.model.PatientRegistrationDto;
import com.aitherapist.aitherapist.domain.model.entities.*;
import com.aitherapist.aitherapist.interactionWithGigaApi.inputParser.ParseUserPrompt;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.ClientRegistrationState;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;

public class RegistrationProcess {
    @Autowired
    public static Verification verification;
    @Autowired
    private static ParseUserPrompt parseUserPrompt;

    public static SendMessage acceptOrEditMedicalInitData(InitialHealthData initialHealthData, Update update, User patient) {
        String genderDisplay = patient.getGender() ? "♂ Мужской" : "♀ Женский";

        // Format birth date and age information
        String birthDateAndAge;
        if (patient.getBirthDate() != null) {
            String formattedDate = patient.getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            int age = patient.getAge();
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
                patient.getName(),
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
                .replyMarkup(InlineKeyboardFactory.createAcceptOrEditKeyboardPatient())
                .build();

    }

    public static SendMessage requestPhoneNumber(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(Answers.PLEASE_GIVE_TELEPHONE_NUMBER.getMessage())
                .replyMarkup(verification.createContactRequestKeyboard())
                .build();
    }

    private static void fillCommonPatientFields(Patient patient, Long userId, PatientRegistrationDto dto, RegistrationContext registrationContext) {
        patient.setPhoneNumber(registrationContext.getTelephone(userId));
        patient.setName(dto.getName());
        patient.setBirthDate(dto.getBirthDate());
        patient.setGender(dto.getGender());
        patient.setTelegramId(userId);

        InitialHealthData healthData = new InitialHealthData();
        healthData.setArrhythmia(dto.getArrhythmia());
        healthData.setHeight(dto.getHeight());
        healthData.setWeight(dto.getWeight());
        healthData.setChronicDiseases(dto.getChronicDiseases());
        healthData.setBadHabits(dto.getBadHabits());
        healthData.setPatient(patient);

        patient.setInitialData(healthData);
    }

    public static SendMessage handleQuestionnaire(Update update, RegistrationContext registrationContext, Long userId, UserServiceImpl userService, ObjectMapper mapper, boolean isClinicPatient) throws TelegramApiException, InterruptedException, JsonProcessingException {
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
                System.out.println("STATE " + state.getBase().toString());
                String response = parseUserPrompt.patientRegistrationParser(state.getBase().toString() );
                String jsonWithType;
                if (isClinicPatient) {
                    jsonWithType = "{\"user_type\":\"CLINIC_PATIENT\"," + response.substring(1);
                } else {
                    jsonWithType = "{\"user_type\":\"BOT_PATIENT\"," + response.substring(1);
                }

                PatientRegistrationDto dto = mapper.readValue(jsonWithType, PatientRegistrationDto.class);
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
                    System.err.println("Ошибка сохранения попробуйте снова... ");
                    e.printStackTrace();
                    throw e;
                }

                registrationContext.clearClientRegistrationState(userId);
                return acceptOrEditMedicalInitData(patient.getInitialData(), update, patient);
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
}
