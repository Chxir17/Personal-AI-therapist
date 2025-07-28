package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.services.DoctorServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class Invite implements ICommand {

    private final DoctorServiceImpl doctorService;

    @Autowired
    public Invite(DoctorServiceImpl doctorService) {
        this.doctorService = doctorService;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long patientId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);

        if (update.hasCallbackQuery()) {
            String[] parts = update.getCallbackQuery().getData().split(" ");
            if (parts.length == 3 && "invite".equals(parts[0])) {
                Long doctorId = Long.parseLong(parts[1]);
                String action = parts[2];

                if ("send".equals(action)) {
                    return createSuccessMessage(chatId, "✅ Приглашение отправлено врачу");
                } else if ("cancel".equals(action)) {
                    return createProfileMessage(chatId);
                }
            }
        }

        List<Doctor> doctors = doctorService.getAllDoctors();

        if (doctors.isEmpty()) {
            return createErrorMessage(chatId, "👨⚕️ В системе пока нет зарегистрированных врачей");
        }

        return createDoctorsListMessage(chatId, doctors);
    }

    private SendMessage createErrorMessage(Long chatId, String errorMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(errorMessage);
        message.setReplyMarkup(InlineKeyboardFactory.createReturnToMenu());
        return message;
    }

    private SendMessage createSuccessMessage(Long chatId, String successMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(successMessage);
        message.setReplyMarkup(InlineKeyboardFactory.createProfileKeyboard());
        return message;
    }

    private SendMessage createProfileMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🔙 Возврат в профиль");
        message.setReplyMarkup(InlineKeyboardFactory.createProfileKeyboard());
        return message;
    }

    private SendMessage createDoctorsListMessage(Long chatId, List<Doctor> doctors) {
        StringBuilder messageText = new StringBuilder();
        messageText.append("👨⚕️ <b>Список врачей</b>\n\n");
        messageText.append("👇 <i>Выберите врача для отправки приглашения:</i>\n\n");

        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            messageText.append(String.format(
                    "%d. <b>%s</b> (%s)\n",
                    i + 1,
                    doctor.getName(),
                    doctor.getLicenseNumber()
            ));
        }

        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createDoctorsInvitationKeyboard(doctors);

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(messageText.toString());
        message.setReplyMarkup(keyboard);
        message.enableHtml(true);

        return message;
    }
}