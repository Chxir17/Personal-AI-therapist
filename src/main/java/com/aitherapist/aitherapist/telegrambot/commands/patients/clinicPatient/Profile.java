package com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient;

import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.Period;

@Component
public class Profile implements ICommand {

    private final UserServiceImpl userService;

    @Autowired
    public Profile(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public SendMessage apply(Update update, RegistrationContext registrationContext) {
        Long userId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);

        ClinicPatient patient = userService.getClinicPatientById(userId);
        if (patient == null) {
            return buildErrorMessage(chatId);
        }

        return buildProfileMessage(chatId, patient);
    }

    private SendMessage buildErrorMessage(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text("❌ Профиль не найден")
                .build();
    }

    private SendMessage buildProfileMessage(Long chatId, ClinicPatient patient) {
        InitialHealthData initialData = patient.getInitialData();

        String message = String.format(
                """
                🏥 *Ваш медицинский профиль*
                
                👤 *Основная информация*
                ├ Имя: %s
                ├ Возраст: %d лет
                ├ Номер телефона: %s
                ├ Пол: %s
                └ Номер медкарты: %s
                
                🩺 *Медицинские показатели*
                %s
                
                ✏️ Вы можете изменить данные через меню профиля
                """,
                escapeMarkdown(patient.getName()),
                patient.getAge(),
                escapeMarkdown(patient.getPhoneNumber()),
                patient.getGender() ? "Мужской ♂" : "Женский ♀",
                patient.getMedicalCardNumber() != null ?
                        escapeMarkdown(patient.getMedicalCardNumber()) : "не указан",
                buildHealthDataSection(initialData)
        );

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(message)
                .parseMode("MarkdownV2")
                .replyMarkup(InlineKeyboardFactory.createProfileKeyboard())
                .build();
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
                └ Аритмия: %s
                """,
                initialData.getHeight(),
                initialData.getWeight(),
                escapeMarkdown(initialData.getChronicDiseases() != null ?
                        initialData.getChronicDiseases() : "нет"),
                escapeMarkdown(initialData.getBadHabits() != null ?
                        initialData.getBadHabits() : "нет"),
                initialData.getHeartPain() != null ?
                        (initialData.getHeartPain() ? "да" : "нет") : "не указано",
                initialData.getArrhythmia() != null ?
                        (initialData.getArrhythmia() ? "да" : "нет") : "не указано"
        );
    }

    private String escapeMarkdown(String text) {
        if (text == null) return "";
        return text.replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
    }
}