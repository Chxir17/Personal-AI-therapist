package com.aitherapist.aitherapist.telegrambot.commands;

import com.aitherapist.aitherapist.db.dao.logic.UserRegistrationService;
import com.aitherapist.aitherapist.telegrambot.commands.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.Answers;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.stereotype.Component;

/**
 * StartCommand - send start message to user.
 */
@Component
@RequiredArgsConstructor
public class StartCommand implements ICommand {

    @Autowired
    private UserRegistrationService userRegistrationService;
    private final RegistrationContext registrationContext;


    /**
     * Check is user sign up or no.
     * if no send message and wait answer
     * @param update
     * @return
     */
    @Override
    public SendMessage apply(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        long chatId = update.getMessage().getChatId();
        System.out.println(userId);
        if(!userRegistrationService.isSignUp(Math.toIntExact(userId))) {
            registrationContext.startRegistration(chatId);
            return new SendMessage(String.valueOf(chatId), Answers.INITIAL_MESSAGE_ABOUT_USER.getMessage());
        }
        return new SendMessage(String.valueOf(chatId), Answers.START_MESSAGE.getMessage() );
    }
}