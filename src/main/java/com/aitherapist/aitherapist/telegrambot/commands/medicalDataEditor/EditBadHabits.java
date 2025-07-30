package com.aitherapist.aitherapist.telegrambot.commands.medicalDataEditor;

import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.stereotype.Component;


@Component
public class EditBadHabits extends AbstractEditCommand {
    public EditBadHabits(TelegramMessageSender telegramMessageSender) {
        super(telegramMessageSender, "Измените список вредных привычек:", Status.EDIT_BAD_HABITS);
    }
}