package com.aitherapist.aitherapist.telegrambot.commands.clinicPatient;

import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class SendMessageDoctor implements ICommand {
    public void sendToUserMessage(Long userId, String message, IMessageSender sender) throws TelegramApiException {
        sender.sendMessage(userId, message);
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        return null;
        //FIXME надо доробтать архитектуру юзера наверное чтобы был функционал принять посмотреть сообщения от врача и добавить какие то лист с сообщениями куда это будет класться и методы к нему
    }
}
