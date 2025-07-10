package com.aitherapist.aitherapist.telegrambot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface IDoctorsComm {
    boolean verify();
    void apply(Update update);
}
