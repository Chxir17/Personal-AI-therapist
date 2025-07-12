package com.aitherapist.aitherapist.telegrambot;

import com.aitherapist.aitherapist.telegrambot.commands.*;
import com.aitherapist.aitherapist.telegrambot.commands.clinicPatient.GetLastMessageFromDoctor;
import com.aitherapist.aitherapist.telegrambot.commands.clinicPatient.SendMessageDoctor;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.ChangeDoctorAccountData;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.GetLastParientMedicalData;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.SendMessageUser;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.settings.SettingsDoctorCommand;
import com.aitherapist.aitherapist.telegrambot.commands.patientSettings.SettingsPatientCommand;
import  com.aitherapist.aitherapist.telegrambot.commands.patientSettings.SettingsPatientCommand;
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
                           ClinicPatientCommand clinicPatientCommand,
                           SettingsPatientCommand settingsPatientCommand,
                           SettingsDoctorCommand settingsDoctorCommand,
                           GetLastMessageFromDoctor getLastMessageFromDoctor,
                           SendMessageUser sendMessagePatient,
                           GetLastParientMedicalData getLastParientMedicalData,
                           SendMessageDoctor sendMessageDoctor,
                           ChangeDoctorAccountData changeDoctorAccountData,
                           com.aitherapist.aitherapist.telegrambot.commands.patients.ChangePatientAccountData changePatientAccountData,
                           ChangeRoleCommand changeRoleCommand) {
        this.commands = Map.ofEntries(
                Map.entry("/start", startCommand),
                Map.entry("/information", informationCommand),
                Map.entry("/role", chooseRoleCommand),
                Map.entry("/doctor", doctorCommand),
                Map.entry("/botPatient", botPatientCommand),
                Map.entry("/clinicPatient", clinicPatientCommand),
                Map.entry("/settingsPatient", settingsPatientCommand),
                Map.entry("/settingsDoctor", settingsDoctorCommand),
                Map.entry("/getLastMessageDoctor", getLastMessageFromDoctor),
                Map.entry("/sendMessageDoctor", sendMessageDoctor),
                Map.entry("/sendMessageToPatient", sendMessagePatient),
                Map.entry("/getLastRecords", getLastParientMedicalData),
                Map.entry("/changeDoctorAccountData", changeDoctorAccountData),
                Map.entry("/changePatientAccountData", changePatientAccountData),
                Map.entry("/changeRole", changeRoleCommand)
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