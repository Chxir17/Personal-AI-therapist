package com.aitherapist.aitherapist.telegrambot.commands.patients;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.functionality.QAChatBot.UserQuestions;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public class QAMode implements ICommand {
    @Autowired
    private PatientServiceImpl patientService;

    private SendMessage QAModeHandler(Update update, Long userId, RegistrationContext registrationContext) throws TelegramApiException {
        try {
            String message = update.getMessage().getText();
            registrationContext.addItemToHistory(userId, message, true);
            String answer = UserQuestions.answerUserQuestion(patientService.findById(userId), message, null);
            registrationContext.addItemToHistory(userId, answer, false);
            InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createBackToMainMenuKeyboard();
            return (SendMessage.builder()
                    .chatId(TelegramIdUtils.getChatId(update).toString())
                    .text(answer)
                    .replyMarkup(keyboard)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        if (registrationContext.getStatus(userId) == Status.QAMode) {
            return QAModeHandler(update, userId, registrationContext);
        }
        registrationContext.setStatus(TelegramIdUtils.extractUserId(update), Status.QAMode);
        return SendMessage.builder()
                .chatId(TelegramIdUtils.getChatId(update).toString())
                .text(Answers.QA_MODE_INIT_MESSAGE.getMessage())
                .replyMarkup(InlineKeyboardFactory.createBackToMenuButton())
                .build();
    }
}
