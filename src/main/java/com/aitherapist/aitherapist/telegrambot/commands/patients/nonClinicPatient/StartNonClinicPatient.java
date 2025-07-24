package com.aitherapist.aitherapist.telegrambot.commands.patients.nonClinicPatient;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.commands.patients.RegistrationProcess;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class StartNonClinicPatient implements ICommand {
    private UserServiceImpl userService;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Autowired
    private RegistrationProcess registrationProcess;
    public Verification verification;
    public PatientServiceImpl patientService;

    @Autowired
    public StartNonClinicPatient(
            PatientServiceImpl patientService,
            UserServiceImpl userService) {
        this.patientService = patientService;
        this.userService = userService;}

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        if (userId == null) {
            return SendMessage.builder()
                    .chatId(TelegramIdUtils.getChatId(update).toString())
                    .text("Не удалось определить пользователя")
                    .build();
        }
        if (registrationContext.getStatus(userId) == Status.REGISTERED_NO_CLINIC_PATIENT) {
            try {
                return registrationProcess.handleQuestionnaire(update, registrationContext, userId, userService, mapper, false);
            } catch (Exception e) {
                return SendMessage.builder()
                        .chatId(TelegramIdUtils.getChatId(update).toString())
                        .text("Ошибка обработки данных")
                        .build();
            }
        } else {
            if (registrationContext.isVerify(userId)) {
                registrationContext.setStatus(userId, Status.REGISTRATION_NO_CLINIC_PATIENT);
                return registrationProcess.requestPhoneNumber(TelegramIdUtils.getChatId(update));
            }
        }
        return SendMessage.builder()
                .chatId(TelegramIdUtils.getChatId(update).toString())
                .text("Выберите действие:")
                .replyMarkup(InlineKeyboardFactory.createPatientDefaultKeyboard(patientService.findById(userId)))
                .build();
    }


}
