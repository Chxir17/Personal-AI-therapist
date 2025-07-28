package com.aitherapist.aitherapist.telegrambot;

import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.*;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.*;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.invite.AcceptInvite;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.invite.RejectInvite;
import com.aitherapist.aitherapist.telegrambot.commands.doctors.settings.SettingsDoctorCommand;
import com.aitherapist.aitherapist.telegrambot.commands.medicalDataEditor.*;
import com.aitherapist.aitherapist.telegrambot.commands.patients.EditPatientAccountData;
import com.aitherapist.aitherapist.telegrambot.commands.patients.QAMode;
import com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient.*;
import com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient.settings.*;
import com.aitherapist.aitherapist.telegrambot.commands.patients.nonClinicPatient.StartNonClinicPatient;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.CommandAccess;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.Map;

/**
 * CommandsHandler - class keep Map with all command, that implement interface ICommand;
 *
 *
 */
@Getter
@Slf4j
@Component
public class CommandsHandler {
    private final Map<String, ICommand> commands;
    private final Verification verification;
    private final TelegramMessageSender messageSender;
    private final UserServiceImpl userService;
    private ITelegramExecutor telegramExecutor;

    @Autowired
    public CommandsHandler(
            StartCommand startCommand,
            SettingsDoctorCommand settingsDoctorCommand,
            InformationCommand informationCommand,
            StartDoctors doctorCommand,
            StartNonClinicPatient botPatientCommand,
            StartClinicPatient clinicPatientCommand,
            DoctorSendMessageToPatient sendMessagePatient,
            HistoryPatients getHistoryPatients,
            PatientsSendMessageToDoctor patientsSendMessageToDoctor,
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
            SetNotificationMessage setNotificationMessage,
            SetNotificationTime setNotificationTime,
            ToggleNotifications toggleNotifications,
            DoctorProfile doctorProfile,
            EditPatientAccountData editMedicalDataCommand,
            ClinicMenu clinicMenu,
            GetLastPatientMedicalData lastRecords,
            QAMode qaMode,
            Help help,
            Privacy privacy,
            EditDoctorAccountData editDoctorAccountData,
            DoctorMenu doctorMenu,
            Verification verification,
            TelegramMessageSender messageSender,
            UserServiceImpl userService,
            GetPatientDailyData getPatientDailyData,
            Invite invite,
            AcceptInvite acceptInvite,
            RejectInvite rejectInvite,
            @Lazy ITelegramExecutor telegramExecutor
    ) {
        this.verification = verification;
        this.messageSender = messageSender;
        this.userService = userService;
        this.commands = createCommandsMap(
                startCommand, settingsDoctorCommand, informationCommand, doctorCommand,
                botPatientCommand, clinicPatientCommand,
                sendMessagePatient, getHistoryPatients, patientsSendMessageToDoctor,
                changeRoleCommand, patientSettings, editNameCommand, editBirthDateCommand,
                editGenderCommand, editArrhythmiaCommand, editChronicDiseasesCommand,
                editHeightCommand, editWeightCommand, editBadHabitsCommand, writeDailyData,
                healthHistory, profile, setNotificationMessage, setNotificationTime,
                toggleNotifications, doctorProfile, editMedicalDataCommand, clinicMenu,
                lastRecords, qaMode, help, privacy, editDoctorAccountData, doctorMenu, getPatientDailyData, invite, acceptInvite, rejectInvite
        );
    }

    private Map<String, ICommand> createCommandsMap(
            StartCommand startCommand,
            SettingsDoctorCommand settingsDoctorCommand,
            InformationCommand informationCommand,
            StartDoctors doctorCommand,
            StartNonClinicPatient botPatientCommand,
            StartClinicPatient clinicPatientCommand,
            DoctorSendMessageToPatient sendMessagePatient,
            HistoryPatients getHistoryPatients,
            PatientsSendMessageToDoctor patientsSendMessageToDoctor,
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
            SetNotificationMessage setNotificationMessage,
            SetNotificationTime setNotificationTime,
            ToggleNotifications toggleNotifications,
            DoctorProfile doctorProfile,
            EditPatientAccountData editMedicalDataCommand,
            ClinicMenu clinicMenu,
            GetLastPatientMedicalData lastRecords,
            QAMode qaMode,
            Help help,
            Privacy privacy,
            EditDoctorAccountData editDoctorAccountData,
            DoctorMenu doctorMenu,
            GetPatientDailyData getPatientDailyData,
            Invite invite,
            AcceptInvite acceptInvite,
            RejectInvite rejectInvite
            ) {
        return Map.ofEntries(
                Map.entry("/start", startCommand),
                Map.entry("/patientDailyData", getPatientDailyData),
                Map.entry("/help", help),
                Map.entry("/privacy", privacy),
                Map.entry("/settingsDoctor", settingsDoctorCommand),
                Map.entry("/DoctorProfile", doctorProfile),
                Map.entry("/getLastRecords", lastRecords),
                Map.entry("/toggleNotification", toggleNotifications),
                Map.entry("/setNotificationTime", setNotificationTime),
                Map.entry("/setNotificationMessage", setNotificationMessage),
                Map.entry("/acceptInitData", doctorMenu),
                Map.entry("/clinicPatientMenu", clinicMenu),
                Map.entry("/QAMode", qaMode),
                Map.entry("/inputDailyData", writeDailyData),
                Map.entry("/information", informationCommand),
                Map.entry("/startDoctor", doctorCommand),
                Map.entry("/botPatient", botPatientCommand),
                Map.entry("/clinicPatient", clinicPatientCommand),
                Map.entry("/patientSettings", patientSettings),
                Map.entry("/sendMessageDoctor", patientsSendMessageToDoctor),
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
                Map.entry("/editDoctorAccountData", editDoctorAccountData),
                Map.entry("/editPatientMedicalData", editMedicalDataCommand),
                Map.entry("/inviteDoctor", invite),
                Map.entry("/acceptInvite", acceptInvite),
                Map.entry("/rejectInvite", rejectInvite)

        );
    }

    public SendMessage handleCommand(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        try {
            String messageText;
            long chatId;
            Long userId;

            if (update.hasCallbackQuery()) {
                messageText = update.getCallbackQuery().getData();
                chatId = update.getCallbackQuery().getMessage().getChatId();
                userId = update.getCallbackQuery().getFrom().getId();
            } else if (update.hasMessage()) {
                messageText = update.getMessage().hasText() ? update.getMessage().getText() : "";
                chatId = update.getMessage().getChatId();
                userId = update.getMessage().getFrom().getId();
            } else {
                log.warn("Unsupported update type");
                return null;
            }

            String command = messageText.contains(" ") ? messageText.split(" ")[0] : messageText;
            ICommand commandHandler = commands.get(command);

            if (commandHandler != null) {
                if (commandHandler.getClass().isAnnotationPresent(CommandAccess.class)) {
                    CommandAccess access = commandHandler.getClass().getAnnotation(CommandAccess.class);

                    if (access.requiresRegistration() && registrationContext.getStatus(userId).isRegistrationProcess()) {
                        return new SendMessage(String.valueOf(chatId), "🔒 Пожалуйста, завершите регистрацию для доступа к этой команде");
                    }
                    Roles userRole = userService.getUserRoles(userId);
                    if (access.allowedRoles().length > 0 &&
                            !Arrays.asList(access.allowedRoles()).contains(userRole)) {
                        return new SendMessage(String.valueOf(chatId), "⛔ Эта команда недоступна для вашей роли");
                    }
                    return commandHandler.apply(update, registrationContext);
                }

                return commandHandler.apply(update, registrationContext, telegramExecutor);
            }

            return new SendMessage(String.valueOf(chatId), "Неизвестная команда");
        } catch (Exception e) {
            log.error("Error handling command", e);
            throw e;
        }
    }

    public SendMessage handleCustomCommand(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        return commands.get("/inputDailyData").apply(update, registrationContext, telegramExecutor);
    }

    public SendMessage handleQaMode(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        return commands.get("/QAMode").apply(update, registrationContext, telegramExecutor);
    }

    public void mapStatusToHandler(Update update, Status status, Long userId, RegistrationContext registrationContext) throws TelegramApiException {
        switch (status) {
            case REGISTRATION_DOCTOR:
                registrationContext.setStatus(userId, Status.REGISTERED_DOCTOR);
                retryCommandExecute(update, registrationContext, "/startDoctor");
                break;
            case REGISTRATION_CLINIC_PATIENT:
                registrationContext.setStatus(userId, Status.REGISTERED_CLINIC_PATIENT);
                retryCommandExecute(update, registrationContext, "/clinicPatient");
                break;
            case REGISTRATION_NO_CLINIC_PATIENT:
                registrationContext.setStatus(userId, Status.REGISTERED_NO_CLINIC_PATIENT);
                retryCommandExecute(update, registrationContext, "/botPatient");
                break;
        }
    }

    public void mapStatusHandler(Update update, Status status, Long userId, RegistrationContext registrationContext) throws TelegramApiException {
        switch (status) {
            case REGISTERED_DOCTOR:
                retryCommandExecute(update, registrationContext, "/startDoctor");
                break;
            case REGISTERED_CLINIC_PATIENT:
                retryCommandExecute(update, registrationContext, "/clinicPatient");
                break;
            case REGISTERED_NO_CLINIC_PATIENT:
                retryCommandExecute(update, registrationContext, "/botPatient");
                break;
        }
    }

    public void handleUserMessageAfterVerificationToFilter(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        mapStatusHandler(update, registrationContext.getStatus(userId), userId, registrationContext);
    }

    public void retryCommandExecute(Update update, RegistrationContext registrationContext, String command) throws TelegramApiException {
        ICommand commandHandler = commands.get(command);
        messageSender.sendMessage(commandHandler.apply(update, registrationContext, telegramExecutor));
    }
}