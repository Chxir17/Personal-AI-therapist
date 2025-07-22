package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class DoctorMenu implements ICommand {


    private final ITelegramExecutor telegramExecutor;

    public DoctorMenu(ITelegramExecutor telegramExecutor) {
        this.telegramExecutor = telegramExecutor;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long chatId = TelegramIdUtils.getChatId(update);

        // ✅ Удаляем сообщение, в котором была нажата кнопка
        if (update.hasCallbackQuery()) {
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

            telegramExecutor.deleteMessage(chatId.toString(), messageId);
        }

        // ✅ Отправляем новое сообщение
        return SendMessage.builder()
                .chatId(chatId)
                .text("⭐ Добро пожаловать! ⭐ \nВыберите команду:")
                .replyMarkup(InlineKeyboardFactory.createDoctorDefaultKeyboard())
                .build();
    }
}
