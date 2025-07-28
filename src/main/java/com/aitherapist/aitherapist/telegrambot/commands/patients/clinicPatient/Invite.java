package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.services.DoctorServiceImpl;
import com.aitherapist.aitherapist.services.PatientServiceImpl;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class Invite implements ICommand {

    private final DoctorServiceImpl doctorService;
    private final TelegramMessageSender telegramMessageSender;
    private final UserServiceImpl userService;
    private final ITelegramExecutor telegramExecutor;

    @Autowired
    public Invite(DoctorServiceImpl doctorService,
                  TelegramMessageSender telegramMessageSender,
                  UserServiceImpl userService,
                  @Lazy ITelegramExecutor telegramExecutor) {
        this.doctorService = doctorService;
        this.telegramMessageSender = telegramMessageSender;
        this.userService = userService;
        this.telegramExecutor = telegramExecutor;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long patientId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);

        if (update.hasCallbackQuery()) {
            String[] parts = update.getCallbackQuery().getData().split(" ");
            if ("/inviteDoctor".equals(parts[0])) {
                if (parts.length == 2) {
                    Long doctorId = Long.parseLong(parts[1]);
                    sendInviteToDoctor(patientId, doctorId);

                    Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
                    telegramExecutor.editMessageText(
                            chatId.toString(),
                            messageId,
                            "✅ Приглашение отправлено врачу",
                            InlineKeyboardFactory.createProfileKeyboard()
                    );
                    return null;
                } else {
                    List<Doctor> doctors = doctorService.getAllDoctors();
                    if (doctors.isEmpty()) {
                        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
                        telegramExecutor.editMessageText(
                                chatId.toString(),
                                messageId,
                                "👨⚕️ В системе пока нет зарегистрированных врачей",
                                InlineKeyboardFactory.createBackToMenuButtonClinic()
                        );
                        return null;
                    }

                    Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
                    telegramExecutor.editMessageText(
                            chatId.toString(),
                            messageId,
                            createDoctorsListMessageText(doctors),
                            InlineKeyboardFactory.createDoctorsInvitationKeyboard(doctors)
                    );
                    return null;
                }
            }
        }

        List<Doctor> doctors = doctorService.getAllDoctors();
        if (doctors.isEmpty()) {
            return createErrorMessage(chatId, "👨⚕️ В системе пока нет зарегистрированных врачей");
        }

        return createDoctorsListMessage(chatId, doctors);
    }

    private String createDoctorsListMessageText(List<Doctor> doctors) {
        StringBuilder messageText = new StringBuilder();
        messageText.append("👨⚕️ <b>Список врачей</b>\n\n");
        messageText.append("👇 Выберите врача для отправки приглашения:\n\n");

        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            messageText.append(String.format(
                    "%d. <b>%s</b> (%s)\n",
                    i + 1,
                    doctor.getName(),
                    doctor.getId()
            ));
        }
        return messageText.toString();
    }

    private void sendInviteToDoctor(Long patientId, Long doctorId) throws TelegramApiException {
        User patient = userService.getUserByUserId(patientId);
        Doctor doctor = doctorService.getDoctor(doctorId);

        if (patient == null || doctor == null) {
            return;
        }

        String messageText = String.format(
                "👋 <b>Новое приглашение</b>\n\n" +
                        "Пациент %s хочет стать вашим подопечным.\n\n" +
                        "Вы хотите принять это приглашение?",
                patient.getName()
        );

        SendMessage inviteMessage = new SendMessage();
        inviteMessage.setChatId(doctor.getTelegramId().toString());
        inviteMessage.setText(messageText);
        inviteMessage.enableHtml(true);
        inviteMessage.setReplyMarkup(InlineKeyboardFactory.createDoctorInviteResponseKeyboard(patientId));

        telegramMessageSender.sendMessage(inviteMessage);
    }

    private SendMessage createErrorMessage(Long chatId, String errorMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(errorMessage);
        message.setReplyMarkup(InlineKeyboardFactory.createBackToMenuButtonClinic());
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
        messageText.append("👨⚕️ Список врачей\n\n");
        messageText.append("👇 Выберите врача для отправки приглашения:\n\n");

        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            messageText.append(String.format(
                    "%d. %s (%s)\n",
                    i + 1,
                    doctor.getName(),
                    doctor.getId()
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