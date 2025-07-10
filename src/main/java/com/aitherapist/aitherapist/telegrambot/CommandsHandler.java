package com.aitherapist.aitherapist.telegrambot;

import com.aitherapist.aitherapist.telegrambot.commands.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.Map;

@Getter
@Slf4j
@Component
public class CommandsHandler {
    private final Map<String, ICommand> commands;

    public CommandsHandler(StartCommand startCommand,
                           InformationCommand informationCommand,
                           ChooseRoleCommand chooseRoleCommand,
                           DoctorCommand doctorCommand,
                           BotPatientCommand botPatientCommand,
                           ClinicPatientCommand clinicPatientCommand) {
        this.commands = Map.of(
                "/start", startCommand,
                "/information", informationCommand,
                "/role", chooseRoleCommand,
                "/doctor", doctorCommand,
                "/botPatient", botPatientCommand,
                "/clinicPatient", clinicPatientCommand
        );
    }

    public SendMessage handleCommand(Update update) throws TelegramApiException {
        String messageText = update.getCallbackQuery().getData();
        String command = messageText.split(" ")[0];
        long chatId = update.getMessage().getChatId();

        var commandHandler = commands.get(command);
        if (commandHandler != null) {
            return commandHandler.apply(update);
        } else {
            log.warn("Unknown command received: {}", command);
            return new SendMessage(String.valueOf(chatId), "Unknown command. Please try again.");
        }
    }
}