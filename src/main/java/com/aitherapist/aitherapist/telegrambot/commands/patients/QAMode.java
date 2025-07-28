package com.aitherapist.aitherapist.telegrambot.commands.patients;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.functionality.QAChatBot.UserQuestions;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.CommandsHandler;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.CommandAccess;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Setter
@Getter
@Component
@CommandAccess(allowedRoles = {Roles.CLINIC_PATIENT, Roles.BOT_PATIENT}, requiresRegistration = true)
public class QAMode implements ICommand {
    private final PatientServiceImpl patientService;
    private final UserQuestions userQuestions;

    @Autowired
    public QAMode(PatientServiceImpl patientService, UserQuestions userQuestions) {
        this.patientService = patientService;
        this.userQuestions = userQuestions;
    }

    private SendMessage QAModeHandler(Update update, Long userId, RegistrationContext registrationContext) throws TelegramApiException {
        try {
            String message = update.getMessage().getText();
            registrationContext.addItemToHistory(userId, message, true);
            String answer = userQuestions.answerUserQuestion(patientService.findById(userId), message, registrationContext.getUserHistory(userId));
            registrationContext.addItemToHistory(userId, answer, false);
            InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createBackToMainMenuKeyboard();
            return (SendMessage.builder()
                    .chatId(TelegramIdUtils.getChatId(update).toString())
                    .text(answer)
                    .replyMarkup(keyboard)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return (SendMessage.builder()
                    .chatId(TelegramIdUtils.getChatId(update).toString())
                    .text("error")
                    .build());
        }
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        if (registrationContext.getStatus(userId) == Status.QAMode) {
            return QAModeHandler(update, userId, registrationContext);
        }

        registrationContext.setStatus(userId, Status.QAMode);
        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createBackToMenuButtonClinic();

        if (update.hasCallbackQuery()) {
            try {
                telegramExecutor.editMessageText(
                        TelegramIdUtils.getChatId(update).toString(),
                        update.getCallbackQuery().getMessage().getMessageId(),
                        Answers.QA_MODE_INIT_MESSAGE.getMessage(),
                        keyboard
                );
                return null;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        return SendMessage.builder()
                .chatId(TelegramIdUtils.getChatId(update).toString())
                .text(Answers.QA_MODE_INIT_MESSAGE.getMessage())
                .replyMarkup(keyboard)
                .build();
    }
}
