package com.aitherapist.aitherapist.telegrambot.commands;

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
    @Override
    public SendMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();

        return new SendMessage(String.valueOf(chatId), Answers.START_MESSAGE.getMessage());
    }
}