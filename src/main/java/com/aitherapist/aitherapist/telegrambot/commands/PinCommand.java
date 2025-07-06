package com.aitherapist.aitherapist.telegrambot.commands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * FIXME: to implement
 * PinCommand - pin main information about user.
 * (name, age, healthData).
 */
@Getter
@Setter
@Component
public class PinCommand implements ICommand {
    @Override
    public SendMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        return null;
    }
}
