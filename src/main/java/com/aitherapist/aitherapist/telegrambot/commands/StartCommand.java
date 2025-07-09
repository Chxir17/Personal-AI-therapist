package com.aitherapist.aitherapist.telegrambot.commands;

import com.aitherapist.aitherapist.db.dao.DataController;
import com.aitherapist.aitherapist.telegrambot.commands.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.Answers;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.stereotype.Component;

/**
 * FIXME: add check: is user reg or already reg.
 * StartCommand - send start message to user.
 */
@Component
@RequiredArgsConstructor
public class StartCommand implements ICommand {
    private DataController dataController;
    private final RegistrationContext registrationContext;


    @Override
    public SendMessage apply(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        long chatId = update.getMessage().getChatId();
        //FIXME: add database function
        //if(!dataController.isSignUp(userId)) {
        if (true) {
            registrationContext.startRegistration(chatId);
            return new SendMessage(String.valueOf(chatId), Answers.INITIAL_MESSAGE_ABOUT_USER.getMessage());
        }

        return new SendMessage(String.valueOf(chatId), Answers.START_MESSAGE.getMessage());
    }
}