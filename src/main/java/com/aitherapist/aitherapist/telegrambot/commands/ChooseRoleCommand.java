package com.aitherapist.aitherapist.telegrambot.commands;

import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
@Slf4j
public class ChooseRoleCommand implements ICommand{

    private IMessageSender messageSender;

    @Autowired
    RegistrationContext registrationContext;

    @Autowired
    private UserServiceImpl userRegistrationService;

    @Override
    public SendMessage apply(Update update) throws TelegramApiException {
        long userId = update.getMessage().getFrom().getId();
        long chatId = update.getMessage().getChatId();
        InlineKeyboardMarkup replyKeyboardDoctor = createInlineRoleKeyboard();


        if(!userRegistrationService.isSignUp(userId)) {
            registrationContext.startRegistration(chatId);
            return new SendMessage(String.valueOf(chatId), Answers.INITIAL_MESSAGE_ABOUT_USER.getMessage());
        }

        messageSender.sendMessage(SendMessage.builder()
                .chatId(update.getMessage().getChatId().toString())
                .text(Answers.PLEASE_GIVE_TELEPHONE_NUMBER.getMessage())
                .replyMarkup(replyKeyboardDoctor)
                .build());
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
