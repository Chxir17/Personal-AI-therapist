package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.db.dao.logic.DoctorRegistrationService;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.commands.IDoctorsCommands;
import com.aitherapist.aitherapist.telegrambot.commands.IVerify;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
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
 * StartDoctors - main class with this functions :
 *  1) verify Doctors
 *  2) send to user some message
 *  3)
 */
@Component
@RequiredArgsConstructor
public class StartDoctors implements IDoctorsCommands, IVerify {

    private String telephoneNumber;
    private IMessageSender messageSender;
    private SendMessageUser sendMessageUser;
    @Autowired
    public StartDoctors(RegistrationContext registrationContext, DoctorRegistrationService doctorRegistationService,
                        TelegramMessageSender messageSender, SendMessageUser sendMessageUser) {
        this.messageSender = messageSender;
        this.sendMessageUser = sendMessageUser;
    }

    /**
     * verify user. If user hide telephone number-> create buttom and request.
     * @param update
     * @return
     * @throws TelegramApiException
     */
    @Override
    public boolean verify(Update update) throws TelegramApiException {
        if (Verification.isContactRequest(update)) {
            messageSender.sendMessage(SendMessage.builder()
                    .chatId(update.getMessage().getChatId().toString())
                    .text(Answers.PLEASE_GIVE_TELEPHONE_NUMBER.getMessage())
                    .replyMarkup(Verification.createContactRequestKeyboard())
                    .build());
        }
        if (Verification.verify(update, this.telephoneNumber)) {
            messageSender.sendMessage(SendMessage.builder().chatId(update.getMessage().getChatId().toString()).text(Answers.VERIFICAATION_SUCCESS.getMessage()).build());
            return true;
        } else {
            messageSender.sendMessage(SendMessage.builder().chatId(update.getMessage().getChatId().toString()).text(Answers.VERIFICAATION_ERROR.getMessage()).build());
            return false;
        }
    }

    /**g
     * Get telephone number and set to field
     * @param update
     */
    @Override
    public void apply(Update update) {
        this.telephoneNumber = update.getMessage().getText();;
    }

    @Override
    public void sendMessageToUser(Long userId, String message) throws TelegramApiException {
        sendMessageUser.sendToUserMessage(userId, message, this.messageSender);
    }

}
