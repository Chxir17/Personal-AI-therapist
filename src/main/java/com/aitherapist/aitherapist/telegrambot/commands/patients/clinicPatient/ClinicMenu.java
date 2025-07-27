package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.repositories.IPatientRepository;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.CommandAccess;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@CommandAccess(allowedRoles = {Roles.CLINIC_PATIENT, Roles.BOT_PATIENT}, requiresRegistration = true)
public class ClinicMenu implements ICommand {
    private final PatientServiceImpl patientService;

    @Autowired
    public ClinicMenu(PatientServiceImpl patientService) {

        this.patientService = patientService;

    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        registrationContext.setStatus(userId, Status.PATIENT_IN_MAIN_MENU);
        Patient patient = patientService.findById(userId);

        return SendMessage.builder()
                .chatId(TelegramIdUtils.getChatId(update))
                .text("✨ Доступные действия ✨  ")
                .replyMarkup(InlineKeyboardFactory.createPatientDefaultKeyboard(patient))
                .build();

    }
}
