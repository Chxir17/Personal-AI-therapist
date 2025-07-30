package com.aitherapist.aitherapist.telegrambot.commands.medicalDataEditor;

import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.stereotype.Component;

@Component
public class EditGender extends AbstractEditCommand {
    public EditGender(TelegramMessageSender telegramMessageSender) {
        super(telegramMessageSender, "Измените пол:", Status.EDIT_GENDER);
    }
}