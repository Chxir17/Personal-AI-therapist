package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.services.PatientServiceImpl;

import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.commands.patients.RegistrationProcess;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class StartClinicPatient implements ICommand {
    private final UserServiceImpl userService;
    private final RegistrationProcess registrationProcess;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private final PatientServiceImpl patientService;

    @Autowired
    public StartClinicPatient(
            RegistrationProcess registrationProcess,
            PatientServiceImpl patientService,
            UserServiceImpl userService) {
        this.patientService = patientService;
        this.userService = userService;
        this.registrationProcess = registrationProcess;
    }


    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        Patient patient = patientService.findById(userId);

        if (userId == null) {
            return SendMessage.builder()
                    .chatId(TelegramIdUtils.getChatId(update).toString())
                    .text("Не удалось определить пользователя")
                    .build();
        }

        if (registrationContext.getStatus(userId) == Status.REGISTERED_CLINIC_PATIENT) {
            try {
                return registrationProcess.handleQuestionnaire(update, registrationContext, userId, userService, mapper, true);
            } catch (Exception e) {
                return SendMessage.builder()
                        .chatId(TelegramIdUtils.getChatId(update).toString())
                        .text("Ошибка обработки данных" + e.getMessage())
                        .build();
            }
        } else {
            if (registrationContext.isVerify(userId)) {
                registrationContext.setStatus(userId, Status.GIVING_PHONE_NUMBER_CLINIC_PATIENT);
                return registrationProcess.requestPhoneNumber(TelegramIdUtils.getChatId(update));
            }
        }
        String messageText = "Вы уже верифицированы. Выберите действие:";
        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createPatientDefaultKeyboard(patient);
        return SendMessage.builder()
                .chatId(TelegramIdUtils.getChatId(update).toString())
                .text(messageText)
                .replyMarkup(keyboard)
                .build();
    }
}
