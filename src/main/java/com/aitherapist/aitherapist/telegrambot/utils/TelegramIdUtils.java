package com.aitherapist.aitherapist.telegrambot.utils;

import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramIdUtils {
    public static Long getChatId(Update update) {
        return update.hasCallbackQuery() ?
                update.getCallbackQuery().getMessage().getChatId() :
                update.getMessage().getChatId();
    }

    public static Long extractUserId(Update update) {
        return update.hasMessage() ?
                update.getMessage().getFrom().getId() :
                update.hasCallbackQuery() ?
                        update.getCallbackQuery().getFrom().getId() :
                        null;
    }
}
