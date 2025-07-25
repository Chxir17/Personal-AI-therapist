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
public class EditDoctorAccountData implements ICommand {

    private final ITelegramExecutor telegramExecutor;
    @Autowired
    private DoctorServiceImpl doctorService;

    public EditDoctorAccountData(@Lazy ITelegramExecutor telegramExecutor) {
        this.telegramExecutor = telegramExecutor;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long chatId = TelegramIdUtils.getChatId(update);

        Doctor doctor = doctorService.getDoctor(TelegramIdUtils.extractUserId(update));

        String genderDisplay = doctor.getGender() ? "♂ Мужской" : "♀ Женский";

        String message = String.format("""
        📝 *Вы ввели данные:*
        
        👤 *Имя:* %s
        🎂 *Возраст:* %d лет
        🚻 *Пол:* %s
        """,
                doctor.getName(),
                doctor.getAge(),
                genderDisplay);

        if (update.hasCallbackQuery()) {
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            telegramExecutor.deleteMessage(chatId.toString(), messageId);
        }

        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createEditDoctorData();

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text("Что вы хотите изменить?")
                .replyMarkup(keyboard)
                .build();
    }
}