package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.CommandAccess;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@CommandAccess(allowedRoles = {Roles.CLINIC_PATIENT, Roles.BOT_PATIENT}, requiresRegistration = true)
public class Profile implements ICommand {

    private final UserServiceImpl userService;

    @Autowired
    public Profile(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);

        Roles role = userService.getUserRoles(userId);
        Patient patient = role == Roles.CLINIC_PATIENT
                ? userService.getClinicPatientById(userId)
                : userService.getNonClinicPatientById(userId);

        if (patient == null) {
            return buildErrorMessage(chatId);
        }

        String messageText = buildProfileMessageText(patient);
        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createProfileKeyboard();

        if (update.hasCallbackQuery()) {
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            try {
                telegramExecutor.editMessageText(
                        chatId.toString(),
                        messageId,
                        messageText,
                        keyboard
                );
                return null;
            } catch (TelegramApiException e) {
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(messageText)
                        .parseMode("HTML")
                        .replyMarkup(keyboard)
                        .build();
            }
        }

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(messageText)
                .parseMode("HTML")
                .replyMarkup(keyboard)
                .build();
    }

    private SendMessage buildErrorMessage(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text("❌ Профиль не найден")
                .build();
    }

    private String buildProfileMessageText(Patient patient) {
        InitialHealthData initialData = patient.getInitialData();
        String doctorInfo = "";

        if (patient instanceof ClinicPatient clinicPatient) {
            if (!clinicPatient.getDoctors().isEmpty()) {
                StringBuilder doctorsBuilder = new StringBuilder("\n👨⚕️ Ваши врачи:\n");
                for (Doctor doctor : clinicPatient.getDoctors()) {
                    doctorsBuilder.append(String.format(
                            "├ %s (%s)\n",
                            doctor.getName(),
                            doctor.getLicenseNumber() != null ? "лиц. " + doctor.getLicenseNumber() : "лицензия не указана"
                    ));
                }
                doctorInfo = doctorsBuilder.toString();
            } else {
                doctorInfo = "\n👨⚕️ У вас нет прикрепленных врачей";
            }
        }

        return String.format(
                """
                🏥 Ваш медицинский профиль
                
                👤 Основная информация
                ├ Имя: %s
                ├ Возраст: %d лет
                ├ Номер телефона: %s
                └ Пол: %s
                
                🩺 Медицинские показатели
                %s
                %s
                
                ✏️ Вы можете изменить данные через меню профиля
                """,
                patient.getName(),
                patient.getAge(),
                patient.getPhoneNumber(),
                patient.getGender() ? "Мужской ♂" : "Женский ♀",
                buildHealthDataSection(initialData),
                doctorInfo
        );
    }

    private String buildHealthDataSection(InitialHealthData initialData) {
        if (initialData == null) {
            return "└ Данные не заполнены";
        }

        return String.format(
                """
                ├ Рост: %.1f см
                ├ Вес: %.1f кг
                ├ Хронические заболевания: %s
                ├ Вредные привычки: %s
                ├ Боли в сердце: %s
                """,
                initialData.getHeight(),
                initialData.getWeight(),
                formatNullable(initialData.getChronicDiseases(), "нет"),
                formatNullable(initialData.getBadHabits(), "нет"),
                formatBoolean(initialData.getHeartPain(), "не указано")
        );
    }

    private String formatNullable(String value, String defaultValue) {
        return value == null || value.isEmpty() || value.equals("false") ? defaultValue : value;
    }

    private String formatBoolean(Boolean value, String defaultValue) {
        if (value == null) return defaultValue;
        return value ? "да" : "нет";
    }
}