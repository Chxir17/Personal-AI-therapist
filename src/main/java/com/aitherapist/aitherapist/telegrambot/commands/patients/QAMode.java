package com.aitherapist.aitherapist.telegrambot.commands.patients;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public class QAMode implements ICommand {
    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        registrationContext.setStatus(TelegramIdUtils.extractUserId(update), Status.QAMode);
        return SendMessage.builder()
                .chatId(TelegramIdUtils.getChatId(update).toString())
                .text(Answers.QA_MODE_INIT_MESSAGE.getMessage())
                .replyMarkup(InlineKeyboardFactory.createBackToMenuButton())
                .build();
    }
}
