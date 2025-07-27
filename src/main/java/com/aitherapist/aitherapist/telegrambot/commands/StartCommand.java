package com.aitherapist.aitherapist.telegrambot.commands;

import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;

import java.util.HashMap;
import java.util.Map;

@Component
public class StartCommand implements ICommand {
    private final IMessageSender messageSender;
    private final UserServiceImpl userRegistrationService;
    private final PatientServiceImpl patientService;

    @Autowired
    public StartCommand(IMessageSender messageSender,
                        UserServiceImpl userRegistrationService,
                        PatientServiceImpl patientService) {
        this.messageSender = messageSender;
        this.userRegistrationService = userRegistrationService;
        this.patientService = patientService;

    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long userId = extractUserId(update);
        Patient patient = patientService.findById(userId);

        if (userId == null) {
            throw new TelegramApiException("Error value. Can't find userId");
        }
        long chatId;

        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else {
            chatId = update.getMessage().getChatId();
        }

        if (!userRegistrationService.isSignUp(userId)) {
            InlineKeyboardMarkup replyKeyboardDoctor = InlineKeyboardFactory.createRoleSelectionKeyboard();

            registrationContext.startRegistration(chatId);

            SendMessage message = new SendMessage(String.valueOf(chatId),
                    Answers.INITIAL_MESSAGE_ABOUT_USER.getMessage());
            message.setParseMode("HTML");

            messageSender.sendMessage(message);

            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("Выберите роль")
                    .replyMarkup(replyKeyboardDoctor)
                    .build();
        }
        registrationContext.start(chatId);
        Roles roles = userRegistrationService.getUserRoles(userId);
        if (roles == Roles.DOCTOR) {
            return SendMessage.builder()
                    .chatId(TelegramIdUtils.getChatId(update))
                    .text(Answers.START_MESSAGE.getMessage())
                    .replyMarkup(InlineKeyboardFactory.createDoctorDefaultKeyboard())
                    .build();
        }
        else {
            return SendMessage.builder().chatId(TelegramIdUtils.getChatId(update)).text(Answers.START_MESSAGE
                    .getMessage()).replyMarkup(InlineKeyboardFactory.createPatientDefaultKeyboard(patient)).build();
        }

    }

    private Long extractUserId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        }
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }
        return null;
    }

}