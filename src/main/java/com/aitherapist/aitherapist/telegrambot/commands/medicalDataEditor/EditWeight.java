package com.aitherapist.aitherapist.telegrambot.commands.medicalDataEditor;

import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.stereotype.Component;

@Component
public class EditWeight extends AbstractEditCommand {
    public EditWeight(TelegramMessageSender telegramMessageSender) {
        super(telegramMessageSender, "Измените вес (в килограммах):", Status.EDIT_WEIGHT);
    }
}