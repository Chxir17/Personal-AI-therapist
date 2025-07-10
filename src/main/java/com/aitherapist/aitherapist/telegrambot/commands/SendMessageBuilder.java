package com.aitherapist.aitherapist.telegrambot.commands;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Builder
@Getter
public class SendMessageBuilder {
    private String chatId;
    private String text;
    private ReplyKeyboardMarkup replyMarkup;
}