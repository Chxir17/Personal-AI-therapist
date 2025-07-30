package com.aitherapist.aitherapist.telegrambot.commands.medicalDataEditor;


import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.stereotype.Component;


@Component
public class EditBirthDate extends AbstractEditCommand {
    public EditBirthDate(TelegramMessageSender telegramMessageSender) {
        super(telegramMessageSender, "Измените возраст:", Status.EDIT_BIRTH_DATE);
    }
}