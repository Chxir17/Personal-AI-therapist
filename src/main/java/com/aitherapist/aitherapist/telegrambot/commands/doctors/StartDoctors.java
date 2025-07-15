package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.commands.IVerify;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StartDoctors implements ICommand {
    @Autowired
    public Verification verification;

    private final IMessageSender messageSender;
    private String expectedPhoneNumber;

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        try {
            Long userId = extractUserId(update);
            if (userId == null) {
                throw new TelegramApiException("Cannot extract user ID from update");
            }
            if (!registrationContext.isVerify(userId)) {
                return requestPhoneNumber(getChatId(update));
            } else {
                InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createDoctorDefaultKeyboard();
                return SendMessage.builder()
                        .chatId(update.getMessage().getChatId().toString())
                        .text("Вы уже верифицированы. Выберите действие:")
                        .replyMarkup(keyboard)
                        .build();
            }

        } catch (Exception e) {
            throw new TelegramApiException("Error processing doctor start", e);
        }
    }

    private SendMessage requestPhoneNumber(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(Answers.PLEASE_GIVE_TELEPHONE_NUMBER.getMessage())
                .replyMarkup(verification.createContactRequestKeyboard())
                .build();
    }



    private Long getChatId(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        } else {
            return update.getMessage().getChatId();
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