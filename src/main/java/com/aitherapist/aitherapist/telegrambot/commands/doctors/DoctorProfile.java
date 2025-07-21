package com.aitherapist.aitherapist.telegrambot.commands.doctors;

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

@Component
public class DoctorProfile implements ICommand {

    private final DoctorServiceImpl doctorService;

    @Autowired
    public DoctorProfile(DoctorServiceImpl doctorService) {
        this.doctorService = doctorService;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext context) {
        Long userId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);

        Doctor doctor = doctorService.getDoctor(userId);

        if (doctor == null) {
            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("❌ Профиль врача не найден")
                    .build();
        }

        String profileText = buildDoctorProfile(doctor);

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(profileText)
                .parseMode("MarkdownV2")
                .replyMarkup(InlineKeyboardFactory.createDoctorProfileKeyboard())
                .build();
    }

    private String buildDoctorProfile(Doctor doctor) {

        return String.format(
                """
                🩺 *Профиль врача* 🏥
                
                👨⚕️ *Основная информация*
                ├ Имя: %s
                ├ Лицензия: %s
                ├ Телефон: %s
                └ Пациентов: %d
                """,
                escapeMarkdown(doctor.getName()),
                escapeMarkdown(doctor.getLicenseNumber() != null ?
                        doctor.getLicenseNumber() : "не указана"),
                escapeMarkdown(doctor.getPhoneNumber() != null ?
                        doctor.getPhoneNumber() : "не указан"),
                doctor.getPatients().size(),
                doctor.getUpdatedAt() != null ?
                        doctor.getUpdatedAt().toString() : "неизвестно"
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