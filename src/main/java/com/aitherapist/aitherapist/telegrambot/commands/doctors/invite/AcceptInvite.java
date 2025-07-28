package com.aitherapist.aitherapist.telegrambot.commands.doctors.invite;

import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.services.DoctorServiceImpl;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.services.interfaces.IDoctorService;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class AcceptInvite implements ICommand {

    private final DoctorServiceImpl doctorService;
    private final PatientServiceImpl patientService;
    private final TelegramMessageSender telegramMessageSender;
    private final UserServiceImpl userService;

    @Autowired
    public AcceptInvite(DoctorServiceImpl doctorService,
                        PatientServiceImpl patientService,
                        TelegramMessageSender telegramMessageSender,
                        UserServiceImpl userService) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.telegramMessageSender = telegramMessageSender;
        this.userService = userService;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        Long doctorId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);

        if (update.hasCallbackQuery()) {
            String[] parts = update.getCallbackQuery().getData().split(" ");
            if (parts.length == 2) {
                Long patientId = Long.parseLong(parts[1]);
                Doctor doctor = doctorService.getDoctor(doctorId);
                Patient patient = patientService.findById(patientId);

                if (doctor != null && patient != null) {
                    doctorService.addPatientToDoctor(
                            userService.getUserByUserId(doctorId).getId(),
                            userService.getUserByUserId(patientId).getId()
                    );

                    SendMessage patientMessage = new SendMessage();
                    patientMessage.setChatId(patient.getTelegramId().toString());
                    patientMessage.setText("✅ Врач принял ваше приглашение!");
                    telegramMessageSender.sendMessage(patientMessage);

                    Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
                    telegramExecutor.editMessageText(
                            chatId.toString(),
                            messageId,
                            "✅ Вы приняли приглашение пациента " + patient.getName(),
                            null
                    );
                    return null;
                }
            }
        }

        return new SendMessage(chatId.toString(), "❌ Не удалось принять приглашение");
    }
}