package com.aitherapist.aitherapist.telegrambot.utils.sender;

import com.aitherapist.aitherapist.telegrambot.TelegramBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramMessageSender implements IMessageSender {
    private final TelegramLongPollingBot bot;

    @Override
    public void sendMessage(long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        bot.execute(message);
    }
}