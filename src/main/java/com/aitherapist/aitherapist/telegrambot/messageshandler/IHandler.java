package com.aitherapist.aitherapist.telegrambot.messageshandler;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * IMessageHandler -
 */
public interface IHandler {
    void handle(Update update, RegistrationContext registrationContext) throws TelegramApiException, JsonProcessingException;
}
