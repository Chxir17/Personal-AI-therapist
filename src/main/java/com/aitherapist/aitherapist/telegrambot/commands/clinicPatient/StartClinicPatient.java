package com.aitherapist.aitherapist.telegrambot.commands.clinicPatient;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.commands.IVerify;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.SendMessageUser;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.Status;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
public class StartClinicPatient implements ICommand {
    private String telephoneNumber;
    private IMessageSender messageSender;
    private SendMessageUser sendMessageUser;

    @Autowired
    public Verification verification;

    @Autowired
    public StartClinicPatient(TelegramMessageSender messageSender, SendMessageUser sendMessageUser) {
        this.messageSender = messageSender;
        this.sendMessageUser = sendMessageUser;
    }

    /**
     * verify user. If user hide telephone number-> create buttom and request.
     * @param update
     * @return
     * @throws TelegramApiException
     */
    /**
     * g
     * Get telephone number and set to field
     *
     * @param update
     * @return
     */
    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        registrationContext.setStatus(update.getMessage().getFrom().getId(), Status.SECOND_PART_REGISTRATION);

//        boolean verStatus = verify(update);
//        while(!verStatus){                //FIXME что с этим делать как это исправить? какой метод тут должен быть теперь?
//            verStatus = verify(update);
//        }
        long chatId = update.getMessage().getChatId();
        return new SendMessage(String.valueOf(chatId), Answers.WRITE_MEDICAL_INFO.getMessage());
    }
}
