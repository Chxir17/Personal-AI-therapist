package com.aitherapist.aitherapist.telegrambot.commands;

import com.aitherapist.aitherapist.telegrambot.utils.Answers;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * FIXME: to implement
 * InformationCommand - get default information from Enum Answers.
 * User pres {/start} and methods send info.
 */
@Getter
@Setter
@Component
public class InformationCommand implements ICommand {
    @Override
    public SendMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        return new SendMessage(String.valueOf(chatId), Answers.INFORMATION_MESSAGE.getMessage());
    }
}
