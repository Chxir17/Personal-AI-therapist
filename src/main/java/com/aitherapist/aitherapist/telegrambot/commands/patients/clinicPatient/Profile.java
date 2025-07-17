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
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long userId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);

        ClinicPatient patient = userService.getClinicPatientById(userId);
        if (patient == null) {
            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("❌ Профиль не найден")
                    .build();
        }

        InitialHealthData initialData = patient.getInitialData();

        StringBuilder message = new StringBuilder();
        message.append("👤 *Ваш профиль*\n\n");

        message.append("📝 *Основная информация:*\n");
        message.append(String.format("• Имя: %s\n", patient.getName()));
        message.append(String.format("• Возраст: %d лет\n", patient.getAge()));
        message.append(String.format("• Пол: %s\n", patient.getGender() ? "Мужской" : "Женский"));
        message.append(String.format("• Телефон: %s\n", patient.getPhoneNumber()));
        message.append(String.format("• Номер медкарты: %s\n\n", patient.getMedicalCardNumber() != null ? patient.getMedicalCardNumber() : "не указан"));

        message.append("🩺 *Медицинские данные:*\n");
        if (initialData != null) {
            message.append(String.format("• Рост: %.1f см\n", initialData.getHeight()));
            message.append(String.format("• Вес: %.1f кг\n", initialData.getWeight()));
            message.append(String.format("• Хронические заболевания: %s\n",
                    initialData.getChronicDiseases() != null ? initialData.getChronicDiseases() : "нет"));
            message.append(String.format("• Вредные привычки: %s\n",
                    initialData.getBadHabits() != null ? initialData.getBadHabits() : "нет"));
            message.append(String.format("• Боли в сердце: %s\n",
                    initialData.getHeartPain() != null ? (initialData.getHeartPain() ? "да" : "нет") : "не указано"));
            message.append(String.format("• Аритмия: %s\n",
                    initialData.getArrhythmia() != null ? (initialData.getArrhythmia() ? "да" : "нет") : "не указано"));
        } else {
            message.append("Медицинские данные не заполнены\n");
        }

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(message.toString())
                .parseMode("MarkdownV2")
                .replyMarkup(InlineKeyboardFactory.createProfileKeyboard())
                .build();
    }
}