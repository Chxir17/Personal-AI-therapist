package com.aitherapist.aitherapist.telegrambot.commands;

import com.aitherapist.aitherapist.services.registration.UserRegistrationService;
import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;

import java.util.List;

@Component
public class StartCommand implements ICommand {
    private final IMessageSender messageSender;
    private final UserRegistrationService userRegistrationService;
    private final RegistrationContext registrationContext;

    @Autowired
    public StartCommand(IMessageSender messageSender, UserRegistrationService userRegistrationService, RegistrationContext registrationContext) {
        this.messageSender = messageSender;
        this.userRegistrationService = userRegistrationService;
        this.registrationContext = registrationContext;
    }

    @Override
    public SendMessage apply(Update update) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        long chatId = update.getMessage().getChatId();

        if(!userRegistrationService.isSignUp(Math.toIntExact(userId))) {
            InlineKeyboardMarkup replyKeyboardDoctor = createInlineRoleKeyboard();
            registrationContext.startRegistration(chatId);
            messageSender.sendMessage(new SendMessage(String.valueOf(chatId),
                    Answers.INITIAL_MESSAGE_ABOUT_USER.getMessage()));

            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("Выберите роль")
                    .replyMarkup(replyKeyboardDoctor)
                    .build();
        }
        return new SendMessage(String.valueOf(chatId), Answers.START_MESSAGE.getMessage());
    }

    private InlineKeyboardMarkup createInlineRoleKeyboard() {
        List<InlineKeyboardButton> row = List.of(
                createButton("Доктор", "/doctor"),
                createButton("Пациент не привязанный к клинике", "/clinicPatient"),
                createButton("Пациент привязанный к клинике", "/botPatient")
        );

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(List.of(row));
        return markup;
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }
}