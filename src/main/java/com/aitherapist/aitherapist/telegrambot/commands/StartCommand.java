package com.aitherapist.aitherapist.telegrambot.commands;

import com.aitherapist.aitherapist.dao.DataController;
import com.aitherapist.aitherapist.telegrambot.utils.Answers;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.stereotype.Component;

/**
 * FIXME: add check: is user reg or already reg.
 * StartCommand - send start message to user.
 */
@Component
public class StartCommand implements ICommand {
    private DataController dataController;

    private Integer getHashId(String tag){
        return tag.hashCode();
    }

    @Override
    public SendMessage apply(Update update) {
        int userId = getHashId(update.getMessage().getFrom().getUserName());
        long chatId = update.getMessage().getChatId();
        if(!dataController.isSignUp(userId)){
            return new SendMessage(String.valueOf(chatId), Answers.INITIAL_MESSAGE_ABOUT_USER.getMessage());
        }
        return new SendMessage(String.valueOf(chatId), Answers.START_MESSAGE.getMessage());
    }
}