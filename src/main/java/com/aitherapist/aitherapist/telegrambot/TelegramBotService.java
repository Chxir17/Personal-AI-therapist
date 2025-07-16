package com.aitherapist.aitherapist.telegrambot;

import com.aitherapist.aitherapist.telegrambot.commands.patients.clinicPatient.WriteDailyData;
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
//import com.aitherapist.aitherapist.telegrambot.scheduled.TelegramNotificationService;
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


    private int flag = 0;
    @Autowired
    WriteDailyData writeDailyData;


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


    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (flag == 1) {
                telegramSender.sendMessage(writeDailyData.apply(update, registrationContext));
                return;
            }
            if (update.hasMessage()) {
                if (update.getMessage().hasText()) {
                    handleMessageUpdate(update, registrationContext);
                } else if (update.getMessage().hasContact()) {
                    handleContactUpdate(update, registrationContext);
                }
            } else if (update.hasCallbackQuery()) {
                if (Objects.equals(update.getCallbackQuery().getMessage().getText(), "Выберите команду:")) {
                    System.out.println("111111");
                    flag = 1;
                    writeDailyData.apply(update, registrationContext);
                }
                handleCallbackQueryUpdate(update, registrationContext);
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