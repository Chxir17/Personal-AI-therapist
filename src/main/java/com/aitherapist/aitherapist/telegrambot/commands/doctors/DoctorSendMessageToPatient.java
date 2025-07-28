package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.domain.enums.Status;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.aitherapist.aitherapist.domain.model.entities.*;
import com.aitherapist.aitherapist.services.DoctorServiceImpl;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

/**
 * DoctorSendMessageToPatient - class print list of doctor clinic patients.
 * print list of map clinit users to buttoms and handle click buttoms.
 */

@Component
public class DoctorSendMessageToPatient implements ICommand {

    @Autowired
    private DoctorServiceImpl doctorService;


    @Override
    @Transactional
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        Long doctorId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);
        if (update.hasCallbackQuery()) {
            String[] parts = update.getCallbackQuery().getData().split(" ");
            if (parts.length == 2) {
                Long patientId = Long.parseLong(parts[1]);
                registrationContext.setStatus(doctorId, Status.WAIT_DOCTOR_WRITE_MESSAGE_TO_USER);

                registrationContext.setStatusWithId(patientId, Status.SEND_TO_THIS_USER, doctorId);
                SendMessage message = new SendMessage();
                message.setChatId(chatId.toString());
                message.setText("✏️ Введите текст сообщения для пациента:");
                message.setReplyMarkup(InlineKeyboardFactory.createReturnToMenu());
                return message;
            }
        }

        if (doctorId == null) {
            return createErrorMessage(chatId, "❌ Ошибка: не удалось определить ваш профиль врача");
        }

        List<Patient> patients = doctorService.getPatients(doctorId);

        if (patients.isEmpty()) {
            return createErrorMessage(chatId, "👨⚕️ У вас пока нет пациентов для отправки сообщений");
        }

        return createPatientsListMessage(chatId, patients);
    }

    private SendMessage createErrorMessage(Long chatId, String errorMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(errorMessage);
        return message;
    }

    private SendMessage createPatientsListMessage(Long chatId, List<Patient> patients) {
        StringBuilder messageText = new StringBuilder();
        messageText.append("💌 <b>Отправка сообщения пациенту</b>\n\n");
        messageText.append("👇 <i>Выберите пациента из списка:</i>\n\n");

        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            messageText.append(String.format(
                    "%d. <b>%s</b> (%d лет, %s)\n <b>%s</b>\n",
                    i + 1,
                    patient.getName(),
                    patient.getAge(),
                    patient.getGender() ? "М" : "Ж",
                    patient.getPhoneNumber()
            ));
        }

        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createPatientsKeyboard(patients);

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(messageText.toString());
        message.setReplyMarkup(keyboard);
        message.enableHtml(true);

        return message;
    }
}
