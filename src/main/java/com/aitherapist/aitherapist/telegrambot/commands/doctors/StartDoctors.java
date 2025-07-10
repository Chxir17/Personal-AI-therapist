package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.db.dao.logic.DoctorRegistrationService;
import com.aitherapist.aitherapist.telegrambot.commands.IDoctorsComm;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.commands.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.Answers;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * StartDoctors - class for verify Doctors.
 */
@Component
@RequiredArgsConstructor
public class StartDoctors implements IDoctorsComm {

    private DoctorRegistrationService doctorRegistationService;
    private RegistrationContext registrationContext;
    private IMessageSender messageSender;
    @Autowired
    public StartDoctors(RegistrationContext registrationContext, DoctorRegistrationService doctorRegistationService,
                        TelegramMessageSender messageSender) {
        this.registrationContext = registrationContext;
        this.doctorRegistationService = doctorRegistationService;
        this.messageSender = messageSender;
    }

    /**
     * verify user. If user hide telephone number-> create buttom and request.
     * @param update
     * @param telephoneNumber
     * @return
     * @throws TelegramApiException
     */
    @Override
    public boolean verify(Update update, String telephoneNumber) throws TelegramApiException {
        if (Verification.isContactRequest(update)) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText(Answers.PLEASE_GIVE_TELEPHONE_NUMBER.getMessage());
            sendMessage.setReplyMarkup(Verification.createContactRequestKeyboard());
            messageSender.sendMessage(sendMessage);
        }

    }

    @Override
    public void apply(Update update) {
        String telephoneNumber =
    }

}
