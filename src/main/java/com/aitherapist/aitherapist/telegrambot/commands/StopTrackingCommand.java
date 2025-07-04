package com.aitherapist.aitherapist.telegrambot.commands;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


@Getter
@Setter
@Component
/**
 * StopTrackingCommand - stop user tracking. delete {HP} information in database
 */
public class StopTrackingCommand implements ICommand {
    @Override
    public SendMessage apply(Update update) {
        return null;
    }
}
