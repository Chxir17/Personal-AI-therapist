package com.aitherapist.aitherapist.telegrambot.commands;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class Privacy implements ICommand {
    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) {
        long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage(String.valueOf(chatId), Answers.PRIVACY_POLICY.getMessage());
        message.enableMarkdown(true);
        return message;
    }
}
