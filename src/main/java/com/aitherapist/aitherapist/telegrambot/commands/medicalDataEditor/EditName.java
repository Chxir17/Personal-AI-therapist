package com.aitherapist.aitherapist.telegrambot.commands.medicalDataEditor;

import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.stereotype.Component;


@Component
public class EditName extends AbstractEditCommand {
    public EditName(TelegramMessageSender telegramMessageSender) {
        super(telegramMessageSender, "Изменить имя на: ", Status.EDIT_WEIGHT);
    }
}