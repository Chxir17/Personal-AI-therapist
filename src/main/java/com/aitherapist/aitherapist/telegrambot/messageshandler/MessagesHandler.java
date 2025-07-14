package com.aitherapist.aitherapist.telegrambot.messageshandler;

import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.domain.model.entities.HealthData;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.interactionWithGigaApi.MakeMedicalRecommendation;
import com.aitherapist.aitherapist.interactionWithGigaApi.ParseUserPrompt;
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
    private final UserServiceImpl userService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    @Lazy
    private IMessageSender messageSender;
    private final ParseUserPrompt parseUserPrompt = new ParseUserPrompt();
    private final MakeMedicalRecommendation makeMedicalRecommendation = new MakeMedicalRecommendation();

    @Override
    public void handle(Update update,RegistrationContext registrationContext) throws TelegramApiException, JsonProcessingException {
        this.registrationContext = registrationContext;
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        long userId = getUserId(update);

        if (registrationContext.isRegistrationInProgress(chatId)) {
            handleRegistration(chatId, userId, messageText);
        }
        else {
            handleHealthData(chatId, userId, messageText, update);
        }
    }

    public SendMessage handleVerify(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        if (registrationContext.isVerify(userId)) {
            return new SendMessage(chatId.toString(), "Ты уже зарегистрирован");
        } else {
            if (Verification.verify(update, update.getMessage().getContact().getPhoneNumber())) {
                registrationContext.setVerify(userId, Status.VERIFIED);
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

    private void handleRegistration(long chatId, long userId, String messageText) throws TelegramApiException {
        try {
            User user = parseUserRegistrationData(messageText);
            registerUser(userId, user);
            sendSuccessMessage(chatId, Answers.REGISTRATION_SUCCESSFUL.getMessage());
            registrationContext.deleteRegistration(chatId);
        } catch (Exception e) {
            logRegistrationError(chatId, e);
        }
    }

    private User parseUserRegistrationData(String messageText) throws JsonProcessingException, InterruptedException {
        String rawJsonResponse = parseUserPrompt.initPromptParser(messageText);
        String cleanJson = cleanJsonResponse(rawJsonResponse);
        return mapper.readValue(cleanJson, User.class);
    }

    private void registerUser(long userId, User user) {
        userService.registerUser(userId, user);
    }

    private void handleHealthData(long chatId, long userId, String messageText, Update update)
            throws TelegramApiException, JsonProcessingException {
        sendInitialResponse(chatId);
        HealthData healthData = parseHealthData(messageText);
        saveHealthData(userId, healthData);
        String recommendation = generateMedicalRecommendation(update);
        if (recommendation != null) {
            sendRecommendation(chatId, recommendation);
        }

    }

    private HealthData parseHealthData(String messageText) throws JsonProcessingException {
        String rawJsonResponse = ParseUserPrompt.dailyQuestionnaireParser(messageText);

        String cleanJson = cleanJsonResponse(rawJsonResponse);
        System.out.println(cleanJson);
        return mapper.readValue(cleanJson, HealthData.class);
    }

    private String cleanJsonResponse(String jsonResponse) {
        return jsonResponse.replaceAll("```json|```", "").trim();
    }

    private void saveHealthData(long userId, HealthData healthData) {
        patientService.addPatientHealthData(userId, healthData);
    }

    private String generateMedicalRecommendation(Update update) {
        User user = userService.getUserByUserId(getUserId(update));
        if (user instanceof Patient patient) {
            return MakeMedicalRecommendation.giveMedicalRecommendation(patient);
        }
        else {
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