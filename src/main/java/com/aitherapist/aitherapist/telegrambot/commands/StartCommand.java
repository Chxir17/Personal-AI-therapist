package com.aitherapist.aitherapist.telegrambot.commands;

import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    private final ITelegramExecutor telegramExecutor;
    @Autowired
    public StartCommand(IMessageSender messageSender,
                        UserServiceImpl userRegistrationService,
                        PatientServiceImpl patientService, @Lazy ITelegramExecutor telegramExecutor) {
        this.messageSender = messageSender;
        this.userRegistrationService = userRegistrationService;
        this.telegramExecutor = telegramExecutor;
        this.patientService = patientService;

    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long userId = extractUserId(update);
        Patient patient = patientService.findById(userId);

        if (userId == null) {
            throw new TelegramApiException("Error value. Can't find userId");
        }

        long chatId = TelegramIdUtils.getChatId(update);

        if (!userRegistrationService.isSignUp(userId)) {
            InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createRoleSelectionKeyboard();
            registrationContext.startRegistration(chatId);

            if (update.hasCallbackQuery()) {
                try {
                    telegramExecutor.editMessageText(
                            String.valueOf(chatId),
                            update.getCallbackQuery().getMessage().getMessageId(),
                            Answers.INITIAL_MESSAGE_ABOUT_USER.getMessage(),
                            keyboard
                    );
                    return null;
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

            SendMessage message = new SendMessage(String.valueOf(chatId),
                    Answers.INITIAL_MESSAGE_ABOUT_USER.getMessage());
            message.setParseMode("HTML");
            messageSender.sendMessage(message);

            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("Выберите роль")
                    .replyMarkup(keyboard)
                    .build();
        }

        registrationContext.start(chatId);
        Roles roles = userRegistrationService.getUserRoles(userId);
        InlineKeyboardMarkup keyboard = roles == Roles.DOCTOR
                ? InlineKeyboardFactory.createDoctorDefaultKeyboard()
                : InlineKeyboardFactory.createPatientDefaultKeyboard(patient);

        if (update.hasCallbackQuery()) {
            try {
                telegramExecutor.editMessageText(
                        String.valueOf(chatId),
                        update.getCallbackQuery().getMessage().getMessageId(),
                        Answers.START_MESSAGE.getMessage(),
                        keyboard
                );
                return null;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(Answers.START_MESSAGE.getMessage())
                .replyMarkup(keyboard)
                .build();
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