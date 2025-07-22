package com.aitherapist.aitherapist.telegrambot.messageshandler;

import com.aitherapist.aitherapist.domain.model.entities.*;
import com.aitherapist.aitherapist.services.*;
import com.aitherapist.aitherapist.functionality.recommendationSystem.MakeMedicalRecommendation;
import com.aitherapist.aitherapist.interactionWithGigaApi.inputParser.ParseUserPrompt;
import com.aitherapist.aitherapist.telegrambot.CommandsHandler;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.scheduled.TelegramNotificationService;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
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
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
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
    InitialHealthDataServiceImpl initialHealthDataServiceImpl;
    @Autowired
    private NotificationServiceImpl notificationService;
    @Autowired
    private final UserServiceImpl userService;
    @Autowired
    private final HealthDataServiceImpl healthDataServiceImpl;
    @Autowired
    private TelegramNotificationService telegramNotificationService;
    @Autowired
    private PatientServiceImpl patientService;
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
        System.out.println("DEB: " + registrationContext.getStatus(userId).toString());
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
        else if (registrationContext.getStatus(userId) == Status.WAIT_DOCTOR_WRITE_MESSAGE_TO_USER) {
            handleMessageFromDoctorToUser(update);
        }
        else if (registrationContext.getStatus(userId) == Status.WRITE_DAILY_DATA) {
            handleWriteDailyData(update);
        }
        if (registrationContext.getStatus(userId)  == Status.SET_NOTIFICATION_TIME) {
            handleSetNotificationTime(update);
        } else if (registrationContext.getStatus(userId) == Status.SET_NOTIFICATION_MESSAGE) {
            handleSetNotificationMessage(update);
        }
        else if(registrationContext.getStatus(userId).isRegistered()){
            commandsHandler.handleUserMessageAfterVerificationToFilter(update, registrationContext);
        }
        else if(registrationContext.getStatus(userId) == Status.REGISTRATION_CLINIC_PATIENT){
            commandsHandler.inProgressQuestionnairePatient(update, registrationContext);
        }
        else if(registrationContext.getStatus(userId) == Status.EDIT_HEIGHT){
            handleEditHeight(update);
        }
        else if(registrationContext.getStatus(userId) == Status.EDIT_WEIGHT){
            handleEditWeight(update);
        }
        else if(registrationContext.getStatus(userId) == Status.EDIT_BAD_HABITS){
            handleEditBadHabits(update);
        }
        else if (registrationContext.getStatus(userId) == Status.GIVING_PATIENT_ID) {
            handleGivePatientIdStatus(update);
        }
    }

    private void handleSetNotificationTime(Update update) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        String timeInput = update.getMessage().getText();

        try {
            LocalTime time = LocalTime.parse(timeInput, DateTimeFormatter.ofPattern("HH:mm"));
            User user = userService.fetchUserByTelegramId(userId);

            notificationService.setNotificationTime(user, time);
            notificationService.setNotificationEnabled(user, true);

            scheduleDailyNotification(chatId, time, userId);

            SendMessage response = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(String.format(
                            "✅ Время уведомления установлено на %s\n\n" +
                                    "Ежедневное напоминание будет приходить в указанное время",
                            time.format(DateTimeFormatter.ofPattern("HH:mm"))))
                    .build();

            messageSender.sendMessage(response);
            registrationContext.setStatus(userId, Status.NONE);
        } catch (DateTimeParseException e) {
            messageSender.sendMessage(chatId,
                    "❌ Неверный формат времени. Пожалуйста, используйте формат HH:mm, например: 09:30");
        } catch (Exception e) {
            messageSender.sendMessage(chatId, "❌ Ошибка при настройке уведомления");
            log.error("Error scheduling notification", e);
        }
    }

    private void scheduleDailyNotification(Long chatId, LocalTime notificationTime, Long userId) {
        try {
            LocalDateTime triggerTime = LocalDateTime.now()
                    .withHour(notificationTime.getHour())
                    .withMinute(notificationTime.getMinute())
                    .withSecond(0);

            if (triggerTime.isBefore(LocalDateTime.now())) {
                triggerTime = triggerTime.plusDays(1);
            }

            String notificationMessage = notificationService.getMessage(userService.fetchUserByTelegramId(userId));
            if (notificationMessage == null || notificationMessage.isEmpty()) {
                notificationMessage = "⏰ Напоминание от вашего терапевта!";
            }

            telegramNotificationService.scheduleNotification(
                    chatId,
                    notificationMessage,
                    triggerTime,
                    userId
            );

        } catch (Exception e) {
            log.error("Failed to schedule daily notification", e);
            throw new RuntimeException("Failed to schedule notification", e);
        }
    }

    private void handleSetNotificationMessage(Update update) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        User user = userService.fetchUserByTelegramId(userId);
        notificationService.setMessage(user, messageText);

        SendMessage response = SendMessage.builder()
                .chatId(chatId.toString())
                .text("✅ Текст уведомления успешно изменен")
                .build();

        messageSender.sendMessage(response);
        registrationContext.setStatus(userId, Status.NONE);
    }

    private void handleWriteDailyData(Update update) throws TelegramApiException {
        messageSender.sendMessage(commandsHandler.handleCustomCommand(update, registrationContext));
    }

    public void handleEditBirthDate(Update update) {
        try {
            String message = update.getMessage().getText();
            String cleanJson = ParseUserPrompt.parameterEditorParser(message);
            User parsedUser = mapper.readValue(cleanJson, User.class);

            Long userId = update.getMessage().getFrom().getId();
            User existingUser = userService.getUserByUserId(userId);

            existingUser.setBirthDate(parsedUser.getBirthDate());
            userService.updateUser(existingUser, userId);
            registrationContext.setStatus(userId, Status.REGISTRATION_DOCTOR);
            acceptOrEditInitInfo(existingUser, update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMessageFromDoctorToUser(Update update) throws TelegramApiException {
        Message message = update.getMessage();
        Long currentDoctorId = message.getFrom().getId();

        List<Long> userIds = registrationContext.findUserIdsWithSendToUserStatus(currentDoctorId);

        for (Long userId : userIds) {
            String doctorMessage = String.format(
                    "✉️ *Вам пришло сообщение от вашего доктора:*\n\n" +
                            "━━━━━━━━━━━━━━━━━━━━\n" +
                            "%s\n" +
                            "━━━━━━━━━━━━━━━━━━━━\n\n" +
                            "Вы можете ответить доктору, просто написав сообщение в этот чат.",
                    message.toString()
            );

            messageSender.sendMessage(1085500451, doctorMessage);
        }
    }

    public void handleEditName(Update update) {
        try {
            String message = update.getMessage().getText();
            String cleanJson = ParseUserPrompt.parameterEditorParser(message);
            User parsedUser = mapper.readValue(cleanJson, User.class);

            Long userId = update.getMessage().getFrom().getId();
            User existingUser = userService.getUserByUserId(userId);

            existingUser.setName(parsedUser.getName());
            userService.updateUser(existingUser, userId);
            registrationContext.setStatus(userId, Status.REGISTRATION_DOCTOR);
            acceptOrEditInitInfo(existingUser, update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleEditGender(Update update) {
        try {
            String message = update.getMessage().getText();
            String cleanJson = ParseUserPrompt.parameterEditorParser(message);
            User parsedUser = mapper.readValue(cleanJson, User.class);

            Long userId = update.getMessage().getFrom().getId();
            User existingUser = userService.getUserByUserId(userId);

            existingUser.setGender(parsedUser.getGender());
            userService.updateUser(existingUser, userId);
            registrationContext.setStatus(userId, Status.REGISTRATION_DOCTOR);
            acceptOrEditInitInfo(existingUser, update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO: add check is verify in db or no
     * @param update
     * @return
     * @throws TelegramApiException
     */



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



    private void acceptOrEditMedicalInitData(InitialHealthData dailyHealthData, Update update) throws TelegramApiException {
        Map<String, String> buttons = new HashMap<>();
        Patient patient = patientService.findById(TelegramIdUtils.extractUserId(update));
        String message = "📝 Вы ввели данные:\n\n" +
                "👤 Имя: " + patient.getName() + "\n" +
                "🎂 Возраст: " + patient.getAge() + " лет\n" +
                "🚻 Пол: " + (patient.getGender() ? "♂ Мужской" : "♀ Женский") + "\n\n" +

                "💓 Аритмия: " + (dailyHealthData.getArrhythmia() ? "Да" : "Нет") + "\n" +
                "🏥 Хронические заболевания: " +
                (dailyHealthData.getChronicDiseases().equalsIgnoreCase("false") ? "Нет" : dailyHealthData.getChronicDiseases()) + "\n" +
                "📏 Рост: " + dailyHealthData.getHeight() + " см\n" +
                "⚖️ Вес: " + dailyHealthData.getWeight() + " кг\n" +
                "🚬 Вредные привычки: " +
                (dailyHealthData.getBadHabits() == null || dailyHealthData.getBadHabits().isEmpty()
                        ? "Нет"
                        : dailyHealthData.getBadHabits()) + "\n\n" +

                "Выберите действие:";
        messageSender.sendMessage(update.getMessage().getChatId(), message);
        buttons.put("Принять", "/acceptClinicPatientInitData");
        buttons.put("Изменить параметры", "/editPatientMedicalData");

        InlineKeyboardMarkup replyKeyboardDoctor = InlineKeyboardFactory.createInlineKeyboard(buttons, 2);

        messageSender.sendMessage(SendMessage.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text("✨ Доступные действия ✨")
                .replyMarkup(replyKeyboardDoctor)
                .build());
    }

    public void handleEditArrhythmia(Update update) {
        try {
            String message = update.getMessage().getText();
            Long userId = update.getMessage().getFrom().getId();

            String cleanJson = ParseUserPrompt.parameterEditorParser(message);
            InitialHealthData initialHealthData = initialHealthDataServiceImpl.getInitialHealthDataByUserId(userId);
            InitialHealthData parsedData = mapper.readValue(cleanJson, InitialHealthData.class);
            initialHealthData.setArrhythmia(parsedData.getArrhythmia());

            initialHealthDataServiceImpl.updateInitialHealthDataByUserId(userId, initialHealthData);
            acceptOrEditMedicalInitData(initialHealthData, update);
        } catch (Exception e) {
            e.printStackTrace(); // лучше логировать
        }
    }

    public void handleEditChronicDiseases(Update update) {
        try {
            String message = update.getMessage().getText();
            Long userId = update.getMessage().getFrom().getId();

            String cleanJson = ParseUserPrompt.parameterEditorParser(message);
            InitialHealthData initialHealthData = initialHealthDataServiceImpl.getInitialHealthDataByUserId(userId);
            InitialHealthData parsedData = mapper.readValue(cleanJson, InitialHealthData.class);
            initialHealthData.setChronicDiseases(parsedData.getChronicDiseases());

            initialHealthDataServiceImpl.updateInitialHealthDataByUserId(userId, initialHealthData);
            acceptOrEditMedicalInitData(initialHealthData, update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleEditHeight(Update update) {
        try {
            String message = update.getMessage().getText();
            Long userId = update.getMessage().getFrom().getId();
            String cleanJson = ParseUserPrompt.parameterEditorParser(message);

            InitialHealthData initialHealthData = initialHealthDataServiceImpl.getInitialHealthDataByUserId(userId);
            InitialHealthData parsedData = mapper.readValue(cleanJson, InitialHealthData.class);
            initialHealthData.setHeight(parsedData.getHeight());

            initialHealthDataServiceImpl.updateInitialHealthDataByUserId(userId, initialHealthData);
            acceptOrEditMedicalInitData(initialHealthData, update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleEditWeight(Update update) {
        try {
            String message = update.getMessage().getText();
            Long userId = update.getMessage().getFrom().getId();
            String cleanJson = ParseUserPrompt.parameterEditorParser(message);

            InitialHealthData initialHealthData = initialHealthDataServiceImpl.getInitialHealthDataByUserId(userId);
            InitialHealthData parsedData = mapper.readValue(cleanJson, InitialHealthData.class);
            initialHealthData.setWeight(parsedData.getWeight());

            initialHealthDataServiceImpl.updateInitialHealthDataByUserId(userId, initialHealthData);
            acceptOrEditMedicalInitData(initialHealthData, update);
        } catch (Exception e) {
            e.printStackTrace(); // TODO: заменить на логгер
        }
    }
    public void handleEditBadHabits(Update update) {
        try {
            String message = update.getMessage().getText();
            Long userId = update.getMessage().getFrom().getId();
            String cleanJson = ParseUserPrompt.parameterEditorParser(message);

            InitialHealthData initialHealthData = initialHealthDataServiceImpl.getInitialHealthDataByUserId(userId);
            InitialHealthData parsedData = mapper.readValue(cleanJson, InitialHealthData.class);
            initialHealthData.setBadHabits(parsedData.getBadHabits());

            initialHealthDataServiceImpl.updateInitialHealthDataByUserId(userId, initialHealthData);
            acceptOrEditMedicalInitData(initialHealthData, update);
        } catch (Exception e) {
            e.printStackTrace(); // TODO: заменить на логгер
        }
    }

    private SendMessage acceptOrEditInitInfo(User user, Update update) {
        String genderDisplay = user.getGender() ? "♂ Мужской" : "♀ Женский";

        String message = String.format("""
        📝 *Вы ввели данные:*
        
        👤 *Имя:* %s
        🎂 *Дата рождения:* %s (%d лет)
        🚻 *Пол:* %s
        """,
                user.getName(),
                user.getBirthDate(),
                user.getAge(),
                genderDisplay);

        return SendMessage.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text(message + "\n\nВыберите действие:")
                .parseMode("Markdown")
                .replyMarkup(InlineKeyboardFactory.createAcceptOrEditKeyboard())
                .build();
    }

    private void handleGivePatientIdStatus(Update update) throws TelegramApiException {

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