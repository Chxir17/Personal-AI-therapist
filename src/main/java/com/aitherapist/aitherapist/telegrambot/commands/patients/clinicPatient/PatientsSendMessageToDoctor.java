package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.functionality.QAChatBot.UserQuestions;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.CommandAccess;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    private final PatientServiceImpl patientService;
    private final TelegramMessageSender telegramMessageSender;


    @Autowired
    public PatientsSendMessageToDoctor(PatientServiceImpl patientService, TelegramMessageSender telegramMessageSender) {
        this.patientService = patientService;
        this.telegramMessageSender = telegramMessageSender;
    }

    @Override
    @Transactional
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);

        if (update.hasCallbackQuery()) {
            String[] parts = update.getCallbackQuery().getData().split(" ");
            if (parts.length == 2) {
                Long doctorId = Long.parseLong(parts[1]);
                registrationContext.setStatus(userId, Status.WAIT_USER_WRITE_MESSAGE_TO_DOCTOR);
                registrationContext.setStatusWithId(doctorId, Status.SEND_TO_THIS_DOCTOR, userId);

                InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createBackToMainMenuKeyboard();

                try {
                    telegramExecutor.editMessageText(
                            chatId.toString(),
                            update.getCallbackQuery().getMessage().getMessageId(),
                            "✏️ Введите текст сообщения для доктора:",
                            keyboard
                    );
                    return null;
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }

        ClinicPatient clinicPatient = (ClinicPatient)patientService.findById(userId);
        List<Doctor> doctors = clinicPatient.getDoctors();

        if (doctors.isEmpty()) {
            telegramMessageSender.sendMessageAndSetToList(createErrorMessage(chatId, "👨⚕️ У вас пока нет докторов!"), registrationContext, userId);
            return null;
        }

        telegramMessageSender.sendMessageAndSetToList( createPatientsListMessage(chatId, doctors), registrationContext, userId);
        return null;
    }

    private SendMessage createErrorMessage(Long chatId, String errorMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(errorMessage);
        return message;
    }

    private SendMessage createPatientsListMessage(Long chatId, List<Doctor> doctors) {
        if (chatId == null) {
            throw new IllegalArgumentException("chatId не может быть null");
        }

        if (doctors == null || doctors.isEmpty()) {
            SendMessage emptyMessage = new SendMessage();
            emptyMessage.setChatId(chatId.toString());
            emptyMessage.setText("🙁 Список врачей пуст.");
            emptyMessage.enableHtml(true);
            return emptyMessage;
        }

        StringBuilder messageText = new StringBuilder();
        messageText.append("💌 <b>Отправка сообщения врачу</b>\n\n");
        messageText.append("👇 <i>Выберите врача из списка:</i>\n\n");

        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            if (doctor == null) continue;

            String name = doctor.getName() != null ? doctor.getName() : "Без имени";
            Integer age = doctor.getAge() != null ? doctor.getAge() : 0;
            String gender = doctor.getGender() != null ? (doctor.getGender() ? "М" : "Ж") : "N/A";
            String phone = doctor.getPhoneNumber() != null ? doctor.getPhoneNumber() : "Телефон не указан";

            messageText.append(String.format(
                    "%d. <b>%s</b> (%d лет, %s)\n <b>%s</b>\n",
                    i + 1,
                    name,
                    age,
                    gender,
                    phone
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
