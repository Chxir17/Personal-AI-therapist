package com.aitherapist.aitherapist.telegrambot.commands.medicalDataEditor;


import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.stereotype.Component;

@Component
public class EditChronicDiseases extends AbstractEditCommand {
    public EditChronicDiseases(TelegramMessageSender telegramMessageSender) {
        super(telegramMessageSender, "Измените список хронических заболеваний:", Status.EDIT_WEIGHT);
    }
}