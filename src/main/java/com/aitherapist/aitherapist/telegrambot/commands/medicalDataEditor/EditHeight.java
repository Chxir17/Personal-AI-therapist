package com.aitherapist.aitherapist.telegrambot.commands.medicalDataEditor;

import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.stereotype.Component;

@Component
public class EditHeight extends AbstractEditCommand {
    public EditHeight(TelegramMessageSender telegramMessageSender) {
        super(telegramMessageSender, "Измените рост (в сантиметрах):", Status.EDIT_HEIGHT);
    }
}