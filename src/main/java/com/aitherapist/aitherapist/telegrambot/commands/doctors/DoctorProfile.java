package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.services.DoctorServiceImpl;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class DoctorProfile implements ICommand {

    private final DoctorServiceImpl doctorService;

    @Autowired
    public DoctorProfile(DoctorServiceImpl doctorService) {
        this.doctorService = doctorService;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext context, ITelegramExecutor telegramExecutor) throws TelegramApiException {
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
        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createDoctorProfileKeyboard();

        if (update.hasCallbackQuery()) {
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            try {
                // Редактируем существующее сообщение
                telegramExecutor.editMessageText(
                        chatId.toString(),
                        messageId,
                        profileText,
                        keyboard
                );
                return null;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(profileText)
                .parseMode("MarkdownV2")
                .replyMarkup(keyboard)
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