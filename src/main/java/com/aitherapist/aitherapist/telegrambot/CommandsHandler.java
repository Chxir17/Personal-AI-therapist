package com.aitherapist.aitherapist.telegrambot;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.telegrambot.commands.*;
import com.aitherapist.aitherapist.telegrambot.commands.clinicPatient.GetLastMessageFromDoctor;
import com.aitherapist.aitherapist.telegrambot.commands.clinicPatient.SendMessageDoctor;
import com.aitherapist.aitherapist.telegrambot.commands.clinicPatient.StartClinicPatient;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.GetLastParientMedicalData;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.HistoryPatients;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.SendMessageUser;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.StartDoctors;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.settings.SettingsDoctorCommand;
import com.aitherapist.aitherapist.telegrambot.commands.initDataEditor.*;
import com.aitherapist.aitherapist.telegrambot.commands.medicalEditor.*;
import com.aitherapist.aitherapist.telegrambot.commands.nonClinicPatient.StartNonClinicPatient;
import com.aitherapist.aitherapist.telegrambot.commands.patientSettings.ChangePatientAccountData;
import com.aitherapist.aitherapist.telegrambot.commands.patientSettings.SettingsPatientCommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    public Verification verification;
    @Autowired
    public TelegramMessageSender messageSender;

    public CommandsHandler(
            StartCommand startCommand,
            InformationCommand informationCommand,
            StartDoctors doctorCommand,
            StartNonClinicPatient botPatientCommand,
            StartClinicPatient clinicPatientCommand,
            SettingsPatientCommand settingsPatientCommand,
            SettingsDoctorCommand settingsDoctorCommand,
            GetLastMessageFromDoctor getLastMessageFromDoctor,
            SendMessageUser sendMessagePatient,
            HistoryPatients getLastParientMedicalData,
            SendMessageDoctor sendMessageDoctor,
            ChangePatientAccountData changeDoctorAccountData,
            ChangePatientAccountData changePatientAccountData,
            ChangeRoleCommand changeRoleCommand,

            EditName editNameCommand,
            EditBirthDate editBirthDateCommand,
            EditGender editGenderCommand,

            EditArrhythmia editArrhythmiaCommand,
            EditChronicDiseases editChronicDiseasesCommand,
            EditHeight editHeightCommand,
            EditWeight editWeightCommand,
            EditBadHabits editBadHabitsCommand,

            EditParameters editParametersCommand,
            EditMedicalData editMedicalDataCommand,

            AcceptInitData acceptInitDataCommand,
            AcceptMedicalData acceptMedicalDataCommand
    ) {
        this.commands = Map.ofEntries(
                Map.entry("/start", startCommand),
                Map.entry("/information", informationCommand),
                Map.entry("/startDoctor", doctorCommand),
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
                Map.entry("/changeRole", changeRoleCommand),

                Map.entry("/editName", editNameCommand),
                Map.entry("/editBirthDate", editBirthDateCommand),
                Map.entry("/editGender", editGenderCommand),

                Map.entry("/editArrhythmia", editArrhythmiaCommand),
                Map.entry("/editChronicDiseases", editChronicDiseasesCommand),
                Map.entry("/editHeight", editHeightCommand),
                Map.entry("/editWeight", editWeightCommand),
                Map.entry("/editBadHabits", editBadHabitsCommand),

                Map.entry("/editParameters", editParametersCommand),
                Map.entry("/editMedicalData", editMedicalDataCommand),

                Map.entry("/acceptInitData", acceptInitDataCommand),
                Map.entry("/acceptMedicalData", acceptMedicalDataCommand)
        );
    }

    public SendMessage handleCommand(Update update, RegistrationContext registrationContext) throws TelegramApiException {

        try {
            String messageText;
            long chatId;

            if (update.hasCallbackQuery()) {
                messageText = update.getCallbackQuery().getData();
                chatId = update.getCallbackQuery().getMessage().getChatId();
            } else if (update.hasMessage()) {
                messageText = update.getMessage().hasText() ? update.getMessage().getText() : "";
                chatId = update.getMessage().getChatId();
            } else {
                log.warn("Unsupported update type");
                return null;
            }


            String command = messageText.contains(" ") ? messageText.split(" ")[0] : messageText;
            ICommand commandHandler = commands.get(command);

            if (commandHandler != null) {
                return commandHandler.apply(update, registrationContext);
            } else {
                return new SendMessage(String.valueOf(chatId), "Неизвестная команда");
            }
        } catch (Exception e) {
            log.error("Error handling command", e);
            throw e;
        }
    }
}