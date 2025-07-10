package com.aitherapist.aitherapist.telegrambot;

import com.aitherapist.aitherapist.telegrambot.commands.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

/**
 * CommandsHandler - registration list of commands handler.
 * Keep list of available commands in Map(str to obj Command)
 */
@Getter
@Setter
@Slf4j
@Component
public class CommandsHandler {
    private final Map<String, ICommand> commands;
    public CommandsHandler(@Autowired StartCommand startCommand,
                           @Autowired InformationCommand informationCommand, @Autowired ChooseRoleCommand chooseRoleCommand) {
        this.commands = Map.of("/start", startCommand, "/information", informationCommand,
                 "/role", chooseRoleCommand);
    }


    /**
     * handleCommand - find command in map commands, and execute the found command
     * @param update
     * @return
     */
    public SendMessage handleCommand(Update update) {
        String messageText = update.getMessage().getText();
        String command = messageText.split(" ")[0];
        long chatId = update.getMessage().getChatId();
        var commandHandler = commands.get(command);
        if (commandHandler != null) {
            return commandHandler.apply(update);
        } else {
            return new SendMessage(String.valueOf(chatId), "unknown command not found");
        }
    }
}
