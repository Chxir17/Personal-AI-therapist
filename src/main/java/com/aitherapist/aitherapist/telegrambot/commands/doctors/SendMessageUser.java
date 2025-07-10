package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * FIXME: add some implementation.
 * SendMessageUser -
 */
@Component
@Getter
@Setter
public class SendMessageUser {

    public void sendToUserMessage(Long userId, String message, IMessageSender sender) throws TelegramApiException {
        sender.sendMessage(userId, message);
    }

}
