package com.aitherapist.aitherapist.telegrambot;

import com.aitherapist.aitherapist.telegrambot.messageshandler.MessagesHandler;
import com.aitherapist.aitherapist.config.BotProperties;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.springframework.context.annotation.Lazy;
import com.aitherapist.aitherapist.domain.enums.HypertensionQA;
import com.aitherapist.aitherapist.telegrambot.scheduled.TelegramNotificationService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Objects;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot implements ITelegramExecutor {
    private final BotProperties botProperties;
    private final CommandsHandler commandsHandler;
    private final @Lazy MessagesHandler messagesHandler;
    private final TelegramNotificationService notificationService;
    private final TelegramMessageSender telegramSender; // Предполагаем, что у вас есть этот интерфейс
    @Autowired
    private RegistrationContext registrationContext;

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }

    //FIXME: добавить куда-то:
    // time - имеет формат час:минуты
    public void notificationFunction(Update update, String time) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        try {
            String[] parts = time.split(" ");
            String timeString = parts[parts.length - 1];

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            var parsedTime = formatter.parse(timeString);

            LocalDateTime triggerTime = LocalDateTime.now()
                    .withHour(parsedTime.get(ChronoField.HOUR_OF_DAY))
                    .withMinute(parsedTime.get(ChronoField.MINUTE_OF_HOUR))
                    .withSecond(0);

            if (triggerTime.isBefore(LocalDateTime.now())) {
                triggerTime = triggerTime.plusDays(1);
            }

            HypertensionQA[] articles = HypertensionQA.values();
            HypertensionQA randomArticle = articles[new Random().nextInt(articles.length)];

            String articleText = String.format("%s\n\n%s",
                    randomArticle.getQuestion(),
                    randomArticle.getAnswer());

            Long notificationId = notificationService.scheduleNotification(
                    chatId,
                    articleText,
                    triggerTime,
                    null
            );

            String confirmation = String.format(
                    "✅ Статья о гипертонии будет отправлена в %s:\n\n<b>%s</b>",
                    formatter.format(triggerTime),
                    randomArticle.getQuestion()
            );

            telegramSender.sendMessage(chatId, confirmation);

        } catch (DateTimeParseException e) {
            telegramSender.sendMessage(chatId, "⏰ Неверный формат времени. Используйте HH:mm, например: 15:30");
        } catch (Exception e) {
            telegramSender.sendMessage(chatId, "❌ Ошибка при планировании уведомления");
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleMessageUpdate(update, registrationContext);
            } else if (update.hasCallbackQuery()) {
                handleCallbackQueryUpdate(update, registrationContext);
            } else if (update.getMessage().hasContact()) {
                handleContactUpdate(update, registrationContext);
            }
        } catch (Exception e) {
            log.error("Error processing update", e);
            sendErrorMessage(update);
        }
    }

    private void handleContactUpdate(Update update, RegistrationContext registrationContext) throws TelegramApiException, JsonProcessingException {
        execute(messagesHandler.handleVerify(update, registrationContext));
    }

    private void handleMessageUpdate(Update update, RegistrationContext registrationContext) throws TelegramApiException, JsonProcessingException, InterruptedException {
        String messageText = update.getMessage().getText();
        if (messageText.startsWith("/")) {
            sendMessage(commandsHandler.handleCommand(update, registrationContext));
        } else {
            messagesHandler.handle(update, registrationContext);
        }
    }

    private void handleCallbackQueryUpdate(Update update, RegistrationContext registrationContext) throws TelegramApiException, JsonProcessingException, InterruptedException {
        String callBackData = update.getCallbackQuery().getData();
        System.out.println("callBackData: " + callBackData);
        if (callBackData != null && callBackData.startsWith("/")) {
            sendMessage(commandsHandler.handleCommand(update, registrationContext));
        } else {
            messagesHandler.handle(update, registrationContext);
        }
    }

    private void sendErrorMessage(Update update) {
        try {
            Long chatId = update.hasMessage() ? update.getMessage().getChatId() :
                    update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() : null;
            if (chatId != null) {
                sendMessage(new SendMessage(chatId.toString(), "Произошла ошибка. Пожалуйста, попробуйте еще раз."));
            }
        } catch (TelegramApiException ex) {
            log.error("Failed to send error message", ex);
        }
    }

    public void sendMessage(@Nullable SendMessage sendMessage) throws TelegramApiException {
        if (sendMessage != null) {
            execute(sendMessage);
        }
    }

    @Override
    public void execute(SendMessage sendMessage) throws TelegramApiException {
        try {
            super.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Send message error", e);
            throw e;
        }
    }
}