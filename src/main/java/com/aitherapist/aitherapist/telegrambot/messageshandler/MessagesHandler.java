package com.aitherapist.aitherapist.telegrambot.messageshandler;

import com.aitherapist.aitherapist.domain.model.FirstPartReg;
import com.aitherapist.aitherapist.domain.model.SecondPartReg;
import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.services.DoctorServiceImpl;
import com.aitherapist.aitherapist.services.HealthDataServiceImpl;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.interactionWithGigaApi.MakeMedicalRecommendation;
import com.aitherapist.aitherapist.interactionWithGigaApi.ParseUserPrompt;
import com.aitherapist.aitherapist.telegrambot.CommandsHandler;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.Status;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.springframework.context.annotation.Lazy;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
@RequiredArgsConstructor
@Slf4j
public class MessagesHandler implements IHandler {
    private final RestTemplate restTemplate = new RestTemplate();
    private RegistrationContext registrationContext;
    @Autowired
    private final Verification verification;
    @Autowired
    PatientServiceImpl patientService;
    @Autowired
    private SecondPartReg secondPartReg;
    @Autowired
    private FirstPartReg firstPartReg;
    @Autowired
    private final UserServiceImpl userService;
    @Autowired
    private final HealthDataServiceImpl healthDataServiceImpl;
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    @Lazy
    private IMessageSender messageSender;
    private final ParseUserPrompt parseUserPrompt = new ParseUserPrompt();
    private final MakeMedicalRecommendation makeMedicalRecommendation = new MakeMedicalRecommendation();
    private DoctorServiceImpl doctorService;
    private PatientServiceImpl patientServiceImpl;
    @Autowired
    private CommandsHandler commandsHandler;

    @Override
    public void handle(Update update, RegistrationContext registrationContext) throws TelegramApiException, JsonProcessingException, InterruptedException {
        this.registrationContext = registrationContext;
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        long userId = getUserId(update);
        System.out.println("in handle mesage");
        if (registrationContext.getStatus(userId) == Status.EDIT_BIRTH_DATE) {
            handleEditBirthDate(update);
        } else if (registrationContext.getStatus(userId) == Status.EDIT_GENDER) {
            handleEditGender(update);
        } else if (registrationContext.getStatus(userId) == Status.EDIT_NAME) {
            handleEditName(update);
        }else if(registrationContext.getStatus(userId) == Status.EDIT_ARRHYTHMIA){
            handleEditArrhythmia(update);
        }
        else if(registrationContext.getStatus(userId) == Status.EDIT_CHRONIC_DISEASES){
            handleEditChronicDiseases(update);
        }
        else if(registrationContext.getStatus(userId) == Status.FIRST_PART_REGISTRATION_DOCTOR){
            commandsHandler.inProgressQuestionnaireDoctor(update, registrationContext);
        }
        else if(registrationContext.getStatus(userId) == Status.EDIT_HEIGHT){
            handleEditHeight(update);
        }
        else if(registrationContext.getStatus(userId) == Status.EDIT_WEIGHT){
            handleEditWeight(update);
        }
        else if(registrationContext.getStatus(userId) == Status.EDIT_BAD_HABITS){
            handleEditBadHabits(update);
        } else if (registrationContext.getStatus(userId) == Status.GIVING_PATIENT_ID) {
            handleGivePatientIdStatus(update);
        } else {
            handleHealthData(chatId, userId, messageText, update);
        }
    }

    public void handleEditBirthDate(Update update) {
        try {
            String message = update.getMessage().getText();
            //String cleanJson = ParseUserPrompt.initPromptParser(message); //FIXME переписать на подходящий метод
            User parsedUser = mapper.readValue("cleanJson", User.class);

            Long userId = update.getMessage().getFrom().getId();
            User existingUser = userService.getUserByUserId(userId);

            existingUser.setBirthDate(parsedUser.getBirthDate());
            userService.updateUser(existingUser, userId);
            registrationContext.setStatus(userId, Status.FIRST_PART_REGISTRATION_DOCTOR);
            acceptOrEditInitInfo(existingUser, update);
        } catch (Exception e) {
            e.printStackTrace(); // лучше логировать
        }
    }

    public void handleEditName(Update update) {
        try {
            String message = update.getMessage().getText();
            //String cleanJson = ParseUserPrompt.initPromptParser(message);//FIXME переписать на подходящий метод
            User parsedUser = mapper.readValue("cleanJson", User.class);

            Long userId = update.getMessage().getFrom().getId();
            User existingUser = userService.getUserByUserId(userId);

            existingUser.setName(parsedUser.getName());
            userService.updateUser(existingUser, userId);
            registrationContext.setStatus(userId, Status.FIRST_PART_REGISTRATION_DOCTOR);
            acceptOrEditInitInfo(existingUser, update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleEditGender(Update update) {
        try {
            String message = update.getMessage().getText();
            //String cleanJson = ParseUserPrompt.initPromptParser(message);//FIXME переписать на подходящий метод
            User parsedUser = mapper.readValue("cleanJson", User.class);

            Long userId = update.getMessage().getFrom().getId();
            User existingUser = userService.getUserByUserId(userId);

            existingUser.setGender(parsedUser.getGender());
            userService.updateUser(existingUser, userId);
            registrationContext.setStatus(userId, Status.FIRST_PART_REGISTRATION_DOCTOR);
            acceptOrEditInitInfo(existingUser, update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SendMessage handleVerify(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        if (registrationContext.isVerify(userId)) {
            return new SendMessage(chatId.toString(), "Ты уже зарегистрирован");
        } else {
            if (Verification.verify(update, update.getMessage().getContact().getPhoneNumber())) {
                registrationContext.setStatus(userId, Status.VERIFIED);
                InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createDoctorDefaultKeyboard();
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("✅ Верификация успешна. Выберите действие:")
                        .replyMarkup(keyboard)
                        .build();
            } else {
                return new SendMessage(chatId.toString(),
                        Answers.VERIFICAATION_ERROR.getMessage());
            }
        }
    }


    public boolean verify(Update update) throws TelegramApiException {
        Long chatId = getChatId(update);
        if (update.hasMessage() && update.getMessage().hasContact()) {
            String actualPhoneNumber = update.getMessage().getContact().getPhoneNumber();
            boolean isValid = verification.verify(update, actualPhoneNumber);

            if (isValid) {
                messageSender.sendMessage(new SendMessage(chatId.toString(),
                        Answers.VERIFICAATION_SUCCESS.getMessage()));
                return true;
            } else {
                messageSender.sendMessage(new SendMessage(chatId.toString(),
                        Answers.VERIFICAATION_ERROR.getMessage()));
                return false;
            }
        }
        return false;
    }

    private Long getChatId(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        } else {
            return update.getMessage().getChatId();
        }
    }

    private long getUserId(Update update) {
        return Math.toIntExact(update.getMessage().getFrom().getId());
    }

//    private void handleFirstPartRegistration(Update update) throws TelegramApiException, InterruptedException, JsonProcessingException {
//        switch (firstPartReg.currentParam) {
//            case (1):
//                firstPartReg.name = update.getMessage().getText();
//                firstPartReg.currentParam++;
//                messageSender.sendMessage(update.getMessage().getChatId(), Answers.AGE.getMessage());
//                break;
//            case (2):
//                firstPartReg.age = update.getMessage().getText();
//                firstPartReg.currentParam++;
//                messageSender.sendMessage(update.getMessage().getChatId(), Answers.GENDER.getMessage());
//                break;
//            case (3):
//                firstPartReg.gender = update.getMessage().getText();
//                System.out.println(firstPartReg.toString());
//                String response = ParseUserPrompt.initPromptParser(firstPartReg.toString());
//                System.out.println(response);
//                String cleanJson = cleanJsonResponse(response);
//                FirstPartReg user = mapper.readValue(cleanJson, FirstPartReg.class);
//                userService.saveUser(user);
//                acceptOrEditInitInfo(user, update);
//
//        }
//    }
//
//    private void handleSecondPartRegistration(Update update)
//            throws TelegramApiException, JsonProcessingException {
//
//        String text = update.getMessage().getText();
//        Long chatId = update.getMessage().getChatId();
//
//        switch (secondPartReg.currentParam) {
//            case 1 -> {
//                secondPartReg.arrhythmia = text;
//                secondPartReg.currentParam++;
//                messageSender.sendMessage(chatId, "Есть ли у вас хронические заболевания?");
//            }
//            case 2 -> {
//                secondPartReg.chronicDiseases = text;
//                secondPartReg.currentParam++;
//                messageSender.sendMessage(chatId, "Введите ваш рост (в сантиметрах):");
//            }
//            case 3 -> {
//                secondPartReg.height = text;
//                secondPartReg.currentParam++;
//                messageSender.sendMessage(chatId, "Введите ваш вес (в килограммах):");
//            }
//            case 4 -> {
//                secondPartReg.weight = text;
//                secondPartReg.currentParam++;
//                messageSender.sendMessage(chatId, "Есть ли у вас вредные привычки?");
//            }
//            case 5 -> {
//                secondPartReg.badHabits = text;
//
//                // Сбор данных завершён — парсим
//                //String response = ParseUserPrompt.initPromptParser(secondPartReg.toString()); //FIXME подставить подходящий парсер
//                //String cleanJson = cleanJsonResponse(response);
//                String cleanJson = "";
//                dailyHealthData healthData = mapper.readValue(cleanJson, dailyHealthData.class);
//
//                Long userId = update.getMessage().getFrom().getId();
//                healthDataServiceImpl.saveHealthDataInUser(userId, healthData);
//                acceptOrEditMedicalInitData(healthData, update);
//            }
//        }
//    }

    public void handleEditArrhythmia(Update update) {
//        try {
//            String message = update.getMessage().getText();
//            String cleanJson = ParseUserPrompt.initPromptParser(message);
//            dailyHealthData parsedData = mapper.readValue(cleanJson, dailyHealthData.class);
//
//            Long userId = update.getMessage().getFrom().getId();
//
//            dailyHealthData healthData = patientService.getPatientHealthData(userId).setArrhythmia(parsedData.getArrhythmia());
//              //FIXME как достать одну хелз дату причём та которая не изменяется каждый день?
//            userService.updateUser(user);
//            acceptOrEditMedicalInitData(healthData, update)
//        } catch (Exception e) {
//            e.printStackTrace(); // лучше логировать
//        }
    }

    public void handleEditChronicDiseases(Update update) {
//        try {
//            String message = update.getMessage().getText();
//            String cleanJson = ParseUserPrompt.initPromptParser(message);
//            dailyHealthData parsedData = mapper.readValue(cleanJson, dailyHealthData.class);
//
//            Long userId = update.getMessage().getFrom().getId();
//            User user = userService.getUserByUserId(userId);
//
//            healthData.setChronicDiseases(parsedData.getChronicDiseases());
//          //FIXME как достать одну хелз дату причём та которая не изменяется каждый день?
//            userService.updateUser(user);
//            acceptOrEditMedicalInitData(healthData, update)
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void handleEditHeight(Update update) {
//        try {
//            String message = update.getMessage().getText();
//            String cleanJson = ParseUserPrompt.initPromptParser(message);
//            dailyHealthData parsedData = mapper.readValue(cleanJson, dailyHealthData.class);
//
//            Long userId = update.getMessage().getFrom().getId();
//            User user = userService.getUserByUserId(userId);
//
//            healthData.setHeight(parsedData.getHeight());
//             //FIXME как достать одну хелз дату причём та которая не изменяется каждый день?
//            userService.updateUser(user);
//            acceptOrEditMedicalInitData(healthData, update)
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void handleEditWeight(Update update) {
//        try {
//            String message = update.getMessage().getText();
//            String cleanJson = ParseUserPrompt.initPromptParser(message);
//            dailyHealthData parsedData = mapper.readValue(cleanJson, dailyHealthData.class);
//
//            Long userId = update.getMessage().getFrom().getId();
//            User user = userService.getUserByUserId(userId);
//            healthData.setWeight(parsedData.getWeight());
//           //FIXME как достать одну хелз дату причём та которая не изменяется каждый день?
//            userService.updateUser(user);
//            acceptOrEditMedicalInitData(healthData, update)
//        } catch (Exception e) {
//            e.printStackTrace();
//      }
    }

    public void handleEditBadHabits(Update update) {
//        try {
//            String message = update.getMessage().getText();
//            String cleanJson = ParseUserPrompt.initPromptParser(message);
//            dailyHealthData parsedData = mapper.readValue(cleanJson, dailyHealthData.class);
//
//            Long userId = update.getMessage().getFrom().getId();
//            User user = userService.getUserByUserId(userId);
//
//            healthData.setBadHabits(parsedData.getBadHabits());
//             //FIXME как достать одну хелз дату причём та которая не изменяется каждый день?
//            userService.updateUser(user);
//            acceptOrEditMedicalInitData(healthData, update)
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

//    private void acceptOrEditMedicalInitData(dailyHealthData dailyHealthData, Update update) throws TelegramApiException {
//        Map<String, String> buttons = new HashMap<>();
//        String message = "Вы ввели:\n Аритмия - " + dailyHealthData.getArrhythmia() + "\n Хронические заболевания - " + dailyHealthData.getChronicDiseases() + "\n Вес - "
//                + dailyHealthData.getHeight() + "\n Вес - " + dailyHealthData.getWeight() + "\n Вредные привычки - " + dailyHealthData.getBadHabits();
//        messageSender.sendMessage(update.getMessage().getChatId(), message);
//        buttons.put("Принять", "/acceptMedicalData");
//        buttons.put("Изменить параметры", "/editMedicalData");
//
//        InlineKeyboardMarkup replyKeyboardDoctor = InlineKeyboardFactory.createInlineKeyboard(buttons, 2);
//
//        messageSender.sendMessage(SendMessage.builder()
//                .chatId(String.valueOf(update.getMessage().getChatId()))
//                .text("Выберите команду")
//                .replyMarkup(replyKeyboardDoctor)
//                .build());
//    }

    private void acceptOrEditInitInfo(User user, Update update) throws TelegramApiException {
        Map<String, String> buttons = new HashMap<>();
        String message = "Вы ввели:\n Имя - " + user.getName() + "\n Дата рождения - " + user.getAge() + "\n Пол - " + user.getGender();
        messageSender.sendMessage(update.getMessage().getChatId(), message);
        buttons.put("Принять", "/acceptInitData");
        buttons.put("Изменить параметры", "/editParameters");

        InlineKeyboardMarkup replyKeyboardDoctor = InlineKeyboardFactory.createInlineKeyboard(buttons, 2);

        messageSender.sendMessage(SendMessage.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text("Выберите команду")
                .replyMarkup(replyKeyboardDoctor)
                .build());
    }

    private void handleGivePatientIdStatus(Update update) throws TelegramApiException {
        long chatId = update.getMessage().getChatId();
        long doctorId = update.getMessage().getFrom().getId();
        String response = update.getMessage().getText();
        long userId = Integer.parseInt(response);
        Patient patient = doctorService.getPatientById(doctorId, userId);
        if (patient == null) {
            messageSender.sendMessage(chatId, "Неверный id пользователя.\n Введите id пациента чтобы получить его послдение измерения");
            return;
        }
        String healthData = "";
        Map<String, String> healthDataHistory = patient.buildMedicalHistory();
        healthData += healthDataHistory.toString();
        messageSender.sendMessage(chatId, healthData);
        Doctor doctor = doctorService.getDoctor(doctorId);
        messageSender.sendMessage(doctor.showDoctorMenu(chatId));
        registrationContext.setStatus(doctorId, Status.NONE);
    }

    private void registerUser(long userId, User user) {
        userService.registerUser(userId, user);
    }

    private void handleHealthData(long chatId, long userId, String messageText, Update update)
            throws TelegramApiException, JsonProcessingException {
        sendInitialResponse(chatId);
        DailyHealthData dailyHealthData = parseHealthData(messageText);
        saveHealthData(userId, dailyHealthData);
        String recommendation = generateMedicalRecommendation(update);
        if (recommendation != null) {
            sendRecommendation(chatId, recommendation);
        }

    }

    private DailyHealthData parseHealthData(String messageText) throws JsonProcessingException {
        String rawJsonResponse = ParseUserPrompt.dailyQuestionnaireParser(messageText);

        String cleanJson = cleanJsonResponse(rawJsonResponse);
        System.out.println(cleanJson);
        return mapper.readValue(cleanJson, DailyHealthData.class);
    }

    private String cleanJsonResponse(String jsonResponse) {
        return jsonResponse.replaceAll("```json|```", "").trim();
    }

    private void saveHealthData(long userId, DailyHealthData dailyHealthData) {
        patientService.addPatientHealthData(userId, dailyHealthData);
    }

    private String generateMedicalRecommendation(Update update) {
        User user = userService.getUserByUserId(getUserId(update));
        if (user instanceof Patient patient) {
            return MakeMedicalRecommendation.giveMedicalRecommendation(patient);
        } else {
            return null;
        }
    }

    private void sendInitialResponse(long chatId) throws TelegramApiException {
        messageSender.sendMessage(chatId, Answers.GIVE_ANSWER.getMessage());
    }

    private void sendRecommendation(long chatId, String recommendation) throws TelegramApiException {
        messageSender.sendMessage(chatId, recommendation);
    }

    private void sendSuccessMessage(long chatId, String message) throws TelegramApiException {
        messageSender.sendMessage(chatId, message);
    }

    private void logRegistrationError(long chatId, Exception e) {
        log.error("Error during registration processing", e);
        try {
            messageSender.sendMessage(chatId, Answers.REGISTRATION_ERROR.getMessage());
        } catch (TelegramApiException ex) {
            log.error("Failed to send error message", ex);
        }
    }

}