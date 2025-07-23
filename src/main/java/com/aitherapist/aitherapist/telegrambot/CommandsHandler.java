package com.aitherapist.aitherapist.telegrambot;

import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.commands.*;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.*;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.settings.SettingsDoctorCommand;
import com.aitherapist.aitherapist.telegrambot.commands.medicalDataEditor.*;
import com.aitherapist.aitherapist.telegrambot.commands.patients.AcceptClinicPatientInitData;
import com.aitherapist.aitherapist.telegrambot.commands.patients.AiDiscussion;
import com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient.*;
import com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient.settings.SetNotificationMessage;
import com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient.settings.SetNotificationTime;
import com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient.settings.Settings;
import com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient.settings.ToggleNotifications;
import com.aitherapist.aitherapist.telegrambot.commands.patients.nonClinicPatient.StartNonClinicPatient;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
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
            SettingsDoctorCommand settingsDoctorCommand,
            InformationCommand informationCommand,
            StartDoctors doctorCommand,
            StartNonClinicPatient botPatientCommand,
            StartClinicPatient clinicPatientCommand,
            GetLastMessageFromDoctor getLastMessageFromDoctor,
            DoctorSendMessageToPatient sendMessagePatient,
            HistoryPatients getHistoryPatients,
            SendMessageDoctor sendMessageDoctor,
            ChangeRoleCommand changeRoleCommand,
            Settings patientSettings,
            EditName editNameCommand,
            EditBirthDate editBirthDateCommand,
            EditGender editGenderCommand,

            EditArrhythmia editArrhythmiaCommand,
            EditChronicDiseases editChronicDiseasesCommand,
            EditHeight editHeightCommand,
            EditWeight editWeightCommand,
            EditBadHabits editBadHabitsCommand,
            WriteDailyData writeDailyData,
            HealthHistory healthHistory,
            Profile profile,
            AiDiscussion aiDiscussion,
            SetNotificationMessage setNotificationMessage,
            SetNotificationTime setNotificationTime,
            ToggleNotifications toggleNotifications,
            DoctorProfile doctorProfile,
//            EditParameters editParametersCommand,
//            EditPatientMedicalData editMedicalDataCommand,
            ClinicMenu clinicMenu,
            GetLastPatientMedicalData lastRecords,
            Help help,
            Privacy privacy,
//            AcceptInitData acceptInitDataCommand,
            DoctorMenu doctorMenu,
            AcceptClinicPatientInitData acceptClinicPatientInitDataCommand
    ) {
        this.commands = Map.ofEntries(
                Map.entry("/start", startCommand),
                Map.entry("/help", help),
                Map.entry("/startAiDiscussion", aiDiscussion),
                Map.entry("/privacy", privacy),
                Map.entry("/settingsDoctor", settingsDoctorCommand),
                Map.entry("/DoctorProfile", doctorProfile),
                Map.entry("/getLastRecords",  lastRecords),
                Map.entry("/toggleNotification", toggleNotifications),
                Map.entry("/setNotificationTime", setNotificationTime),
                Map.entry("/setNotificationMessage", setNotificationMessage),
                Map.entry("/acceptInitData", doctorMenu),
                Map.entry("/acceptInitDataClinic", clinicMenu),
                Map.entry("/inputDailyData", writeDailyData),
                Map.entry("/information", informationCommand),
                Map.entry("/startDoctor", doctorCommand),
                Map.entry("/botPatient", botPatientCommand),
                Map.entry("/clinicPatient", clinicPatientCommand),
                Map.entry("/patientSettings", patientSettings),
                Map.entry("/getLastMessageDoctor", getLastMessageFromDoctor),
                Map.entry("/sendMessageDoctor", sendMessageDoctor),
                Map.entry("/sendMessageToPatient", sendMessagePatient),
                Map.entry("/patientHistory", getHistoryPatients),
                Map.entry("/changeRole", changeRoleCommand),
                Map.entry("/myHealthHistory", healthHistory),
                Map.entry("/editName", editNameCommand),
                Map.entry("/editBirthDate", editBirthDateCommand),
                Map.entry("/editGender", editGenderCommand),
                Map.entry("/myProfile", profile),
                Map.entry("/editArrhythmia", editArrhythmiaCommand),
                Map.entry("/editChronicDiseases", editChronicDiseasesCommand),
                Map.entry("/editHeight", editHeightCommand),
                Map.entry("/editWeight", editWeightCommand),
                Map.entry("/editBadHabits", editBadHabitsCommand),

//                Map.entry("/editParameters", editParametersCommand),
//                Map.entry("/editPatientMedicalData", editMedicalDataCommand),
//
//                Map.entry("/acceptInitData", acceptInitDataCommand),
                Map.entry("/acceptClinicPatientInitData", acceptClinicPatientInitDataCommand)
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

    public SendMessage handleCustomCommand(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        ICommand commandHandler = commands.get("/inputDailyData");
        return commandHandler.apply(update, registrationContext);
    }

    public void mapStatusToHandler(Update update, Status s, Long userId, RegistrationContext registrationContext) throws TelegramApiException {
        if (s == Status.REGISTRATION_DOCTOR) {
            registrationContext.setStatus(userId, Status.REGISTERED_DOCTOR);
            inProgressQuestionnaireDoctor(update, registrationContext);
        }
        else if (s == Status.REGISTRATION_CLINIC_PATIENT) {
            registrationContext.setStatus(userId, Status.REGISTERED_CLINIC_PATIENT);
            inProgressQuestionnairePatient(update, registrationContext);
        } else if (s == Status.REGISTRATION_NO_CLINIC_PATIENT) {
            registrationContext.setStatus(userId, Status.REGISTERED_NO_CLINIC_PATIENT);
            inProgressQuestionnaireNonPatient(update, registrationContext);
        }
    }



    public void mapStatusHandler(Update update, Status s, Long userId, RegistrationContext registrationContext) throws TelegramApiException {
        if (s == Status.REGISTERED_DOCTOR) {
            inProgressQuestionnaireDoctor(update, registrationContext);
        } else if (s == Status.REGISTERED_CLINIC_PATIENT) {
            inProgressQuestionnairePatient(update, registrationContext);
        } else if (s == Status.REGISTERED_NO_CLINIC_PATIENT) {
            inProgressQuestionnaireNonPatient(update, registrationContext);
        }
    }

    public void handleUserMessageAfterVerificationToFilter(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        mapStatusHandler(update, registrationContext.getStatus(userId), userId, registrationContext);
    }

    public void inProgressQuestionnaireDoctor(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        ICommand commandHandler = commands.get("/startDoctor");
        messageSender.sendMessage(commandHandler.apply(update, registrationContext));
    }
    public void inProgressQuestionnairePatient(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        ICommand commandHandler = commands.get("/clinicPatient");
        messageSender.sendMessage(commandHandler.apply(update, registrationContext));
    }

    public void inProgressQuestionnaireNonPatient(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        ICommand commandHandler = commands.get("/botPatient");

        messageSender.sendMessage(commandHandler.apply(update, registrationContext));
    }

    public void lastDailyHealthDataUser(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        ICommand commandHandler = commands.get("/getLastRecords");

        messageSender.sendMessage(commandHandler.apply(update, registrationContext));
    }

}