package com.aitherapist.aitherapist.telegrambot.commands.medicalDataEditor;

import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.stereotype.Component;


@Component
public class EditArrhythmia extends AbstractEditCommand {
    public EditArrhythmia(TelegramMessageSender telegramMessageSender) {
        super(telegramMessageSender, "Измените информацию о нарушении ритма:", Status.EDIT_ARRHYTHMIA);
    }
}