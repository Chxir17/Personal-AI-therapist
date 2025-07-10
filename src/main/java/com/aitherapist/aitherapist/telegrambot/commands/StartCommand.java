package com.aitherapist.aitherapist.telegrambot.commands;

import com.aitherapist.aitherapist.db.dao.logic.UserRegistrationService;
import com.aitherapist.aitherapist.telegrambot.utils.Answers;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;

import java.util.List;


/**
 * StartCommand - send start message to user.
 */
@Component
@RequiredArgsConstructor
public class StartCommand implements ICommand {

    private IMessageSender messageSender;

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private final RegistrationContext registrationContext;

    @Autowired
    private TelegramMessageSender telegramMessageSender;

    /**
     * Check is user sign up or no.
     * if no send message and wait answer
     * @param update
     * @return
     */
    @Override
    public SendMessage apply(Update update) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        long chatId = update.getMessage().getChatId();

        if(!userRegistrationService.isSignUp(Math.toIntExact(userId))) {
            InlineKeyboardMarkup replyKeyboardDoctor = createInlineRoleKeyboard();
            registrationContext.startRegistration(chatId);
            telegramMessageSender.sendMessage(new SendMessage(String.valueOf(chatId), Answers.INITIAL_MESSAGE_ABOUT_USER.getMessage()));
            return SendMessage.builder()
                    .chatId(update.getMessage().getChatId().toString())
                    .text("Выберите роль")
                    .replyMarkup(replyKeyboardDoctor)
                    .build();

        }
        return new SendMessage(String.valueOf(chatId), Answers.START_MESSAGE.getMessage() );
    }

    public InlineKeyboardMarkup createInlineRoleKeyboard() {
        InlineKeyboardButton doctorBtn = new InlineKeyboardButton();
        doctorBtn.setText("Доктор");
        doctorBtn.setCallbackData("/doctor");

        InlineKeyboardButton patientClinicBtn = new InlineKeyboardButton();
        patientClinicBtn.setText("Пациент привязанный к клинике");
        patientClinicBtn.setCallbackData("/botPatient");

        InlineKeyboardButton patientBotBtn = new InlineKeyboardButton();
        patientBotBtn.setText("Пациент не привязанный к клинике");
        patientBotBtn.setCallbackData("/clinicPatient");

        List<InlineKeyboardButton> row = List.of(doctorBtn, patientBotBtn, patientClinicBtn);
        List<List<InlineKeyboardButton>> keyboard = List.of(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        return markup;
    }
}