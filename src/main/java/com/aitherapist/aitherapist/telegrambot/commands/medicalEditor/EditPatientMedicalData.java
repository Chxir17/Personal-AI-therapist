package com.aitherapist.aitherapist.telegrambot.commands.medicalEditor;

import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class EditPatientMedicalData implements ICommand {

    private final IMessageSender messageSender;

    @Autowired
    public EditPatientMedicalData(IMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();

        Map<String, String> buttons = new LinkedHashMap<>();
        buttons.put("Изменить имя", "/editName");
        buttons.put("Изменить дату рождения", "/editBirthDate");
        buttons.put("Изменить пол", "/editGender");
        buttons.put("Аритмия", "/editArrhythmia");
        buttons.put("Хронические заболевания", "/editChronicDiseases");
        buttons.put("Рост", "/editHeight");
        buttons.put("Вес", "/editWeight");
        buttons.put("Вредные привычки", "/editBadHabits");


        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createInlineKeyboard(buttons, 2);

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text("Что вы хотите изменить в медицинских данных?")
                .replyMarkup(keyboard)
                .build();
    }
}