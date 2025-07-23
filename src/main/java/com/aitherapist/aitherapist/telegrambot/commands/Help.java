package com.aitherapist.aitherapist.telegrambot.commands;

import com.aitherapist.aitherapist.telegrambot.utils.sender.IMessageSender;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Help implements ICommand {

    private final IMessageSender messageSender;

    @Autowired
    public Help(IMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) throws TelegramApiException {
        Long chatId = extractChatId(update);

        String helpText = """
                📖 <b>Помощь по использованию бота</b>

                🔹 <b>/start</b> — начало работы. Вы выбираете, кто вы:
                ─ 🩺 Доктор
                ─ 💊 Пациент
                ─ 🏥 Пациент клиники
                ─ ❔ Гость

                🔹 Далее пройдите короткую верификацию.

                🔹 После этого появятся основные кнопки. Выбирайте нужный раздел — и вперед!

                🔄 Если что-то пошло не так — нажмите /start, чтобы всё начать заново.

                💬 Если возникнут вопросы — бот подскажет!
                """;

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(helpText)
                .parseMode("HTML")
                .build();

        messageSender.sendMessage(message);

        return null; // Возврат null, потому что сообщение уже отправлено напрямую
    }

    private Long extractChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        return null;
    }
}
