package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.CommandAccess;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@CommandAccess(allowedRoles = {Roles.CLINIC_PATIENT}, requiresRegistration = true)
@Component
public class PatientsSendMessageToDoctor implements ICommand {

    @Autowired
    private PatientServiceImpl patientService;


    @Override
    @Transactional
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);
        if (update.hasCallbackQuery()) {
            String[] parts = update.getCallbackQuery().getData().split(" ");
            if (parts.length == 2) {
                Long doctorId = Long.parseLong(parts[1]);
                registrationContext.setStatus(userId, Status.WAIT_USER_WRITE_MESSAGE_TO_DOCTOR);

                registrationContext.setStatusWithId(doctorId, Status.SEND_TO_THIS_DOCTOR, userId);

                SendMessage message = new SendMessage();
                message.setChatId(chatId.toString());
                message.setText("✏️ Введите текст сообщения для доктора:");
                message.setReplyMarkup(InlineKeyboardFactory.createBackToMainMenuKeyboard());
                return message;
            }
        }

        ClinicPatient clinicPatient = (ClinicPatient)patientService.findById(userId);
        List<Doctor> doctors = clinicPatient.getDoctors();

        if (doctors.isEmpty()) {
            return createErrorMessage(chatId, "👨⚕️ У вас пока нет докторов!");
        }

        return createPatientsListMessage(chatId, doctors);
    }

    private SendMessage createErrorMessage(Long chatId, String errorMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(errorMessage);
        return message;
    }

    private SendMessage createPatientsListMessage(Long chatId, List<Doctor> doctors) {
        StringBuilder messageText = new StringBuilder();
        messageText.append("💌 <b>Отправка сообщения пациенту</b>\n\n");
        messageText.append("👇 <i>Выберите пациента из списка:</i>\n\n");

        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            messageText.append(String.format(
                    "%d. <b>%s</b> (%d лет, %s)\n <b>%s</b>\n",
                    i + 1,
                    doctor.getName(),
                    doctor.getAge(),
                    doctor.getGender() ? "М" : "Ж",
                    doctor.getPhoneNumber()
            ));
        }

        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createDoctorsKeyboard(doctors);

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(messageText.toString());
        message.setReplyMarkup(keyboard);
        message.enableHtml(true);

        return message;
    }
}
