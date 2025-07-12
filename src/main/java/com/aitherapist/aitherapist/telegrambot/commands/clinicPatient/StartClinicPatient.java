package com.aitherapist.aitherapist.telegrambot.commands.clinicPatient;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.commands.IVerify;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.SendMessageUser;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
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
public class StartClinicPatient implements IVerify, ICommand {
    private String telephoneNumber;
    private IMessageSender messageSender;
    private SendMessageUser sendMessageUser;
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
    @Override
    public boolean verify(Update update) throws TelegramApiException {
        if (Verification.isContactRequest(update)) {
            messageSender.sendMessage(SendMessage.builder()
                    .chatId(update.getMessage().getChatId().toString())
                    .text(Answers.PLEASE_GIVE_TELEPHONE_NUMBER.getMessage())
                    .replyMarkup(Verification.createContactRequestKeyboard())
                    .build());
        }
        this.telephoneNumber = update.getMessage().getText();
        if (Verification.verify(update, this.telephoneNumber)) {
            messageSender.sendMessage(SendMessage.builder().chatId(update.getMessage().getChatId().toString()).text(Answers.VERIFICAATION_SUCCESS.getMessage()).build());
            return true;
        } else {
            messageSender.sendMessage(SendMessage.builder().chatId(update.getMessage().getChatId().toString()).text(Answers.VERIFICAATION_ERROR.getMessage()).build());
            return false;
        }
    }

    /**
     * g
     * Get telephone number and set to field
     *
     * @param update
     * @return
     */
    @Override
    public SendMessage apply(Update update) throws TelegramApiException {
        boolean verStatus = verify(update);
        while(!verStatus){
            verStatus = verify(update);
        }
        long chatId = update.getMessage().getChatId();
        Map<String, String> buttons = new HashMap<>();
        buttons.put("Получить последние сообщения от доктора", "/getLastMessageDoctor");
        buttons.put("Отправить сообщение доктору","/sendMessageDoctor");
        buttons.put("Настройки","/settingsPatient");
        InlineKeyboardMarkup commands = InlineKeyboardFactory.createInlineKeyboard(buttons, 2);
        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Выберите Комманду")
                .replyMarkup(commands)
                .build();
    }
}
