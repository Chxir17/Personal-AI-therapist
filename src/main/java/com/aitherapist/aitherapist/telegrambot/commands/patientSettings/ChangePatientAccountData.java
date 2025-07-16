package com.aitherapist.aitherapist.telegrambot.commands.patientSettings;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class ChangePatientAccountData implements ICommand {
    private final IMessageSender messageSender;

    public ChangePatientAccountData(IMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) {
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
//        Map<String, String> buttons = Map.of(
//                "Сменить возраст", "/changeAge",
//                "Сменить пол", "/changeGender",
//                "Сменить имя", "/changeName",
//                "Сменить рост и вес", "/changeHeightWeight",
//                "Сменить вредные привычки", "/changeHabits"
//        );
//
//        InlineKeyboardMarkup keyboard = InlineKeyboardFactory.createInlineKeyboard(buttons, 1);
        registrationContext.setStatus(userId, Status.REWRITE_PATIENT_PARAMETERS);
        return new SendMessage(String.valueOf(chatId), Answers.REWRITE_USER_PARAMETRS.getMessage());
//FIXME всё таки я бы остановился бы на промте потому что во первых плодить много кнопок не красиво и во вторых бот чтобы
// был чуть умнее типо чтобы из контекста мог вычленять информацию

    }
}