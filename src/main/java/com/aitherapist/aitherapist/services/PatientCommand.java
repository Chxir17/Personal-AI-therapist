package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class PatientCommand implements ICommand {
    @Override
    public String getCommand() {
        return "/patient";
    }

    @Override
    public String getDescription() {
        return "Режим пациента";
    }

    @Override
    public SendMessage execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text("Вы выбрали роль пациента. Пожалуйста, введите ваш номер телефона:")
                .build();
    }
}