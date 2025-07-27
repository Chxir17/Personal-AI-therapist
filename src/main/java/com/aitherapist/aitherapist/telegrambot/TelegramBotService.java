package com.aitherapist.aitherapist.telegrambot;

import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.messageshandler.MessagesHandler;
import com.aitherapist.aitherapist.config.BotProperties;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot implements ITelegramExecutor {
    private final BotProperties botProperties;
    private final CommandsHandler commandsHandler;
    private final @Lazy MessagesHandler messagesHandler;
    @Autowired
    public Verification verification;

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

    public String generateSessionId(Update update) {
        Long chatId = update.hasMessage() ? update.getMessage().getChatId() :
                update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() :
                        null;

        return "chat_" + chatId + "_" + System.currentTimeMillis();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                if (update.getMessage().hasText()) {
                    handleMessageUpdate(update, registrationContext);
                } else if(update.getMessage().hasVoice()) {
                    handleVoiceMessage(update);
                } else if (update.getMessage().hasContact()) {
                    handleContactUpdate(update, registrationContext);
                }
            } else if (update.hasCallbackQuery()) {
                handleCallbackQueryUpdate(update, registrationContext);
            }
        } catch (Exception e) {
            log.error("Error processing update", e);
            sendErrorMessage(update);
        }
    }


    private void handleVoiceMessage(Update update) throws TelegramApiException, IOException, InterruptedException {
        try {
            Voice voice = update.getMessage().getVoice();
            String fileId = voice.getFileId();

            // Получаем файл из Telegram
            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
            String fileUrl = file.getFileUrl(getBotToken());

            // Формируем запрос к FastAPI
            String jsonRequest = String.format("{\"url\":\"%s\"}", fileUrl);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8000/transcribe"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String transcribedText = new ObjectMapper()
                        .readTree(response.body())
                        .get("text")
                        .asText();

                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId().toString());
                message.setText("Расшифровка: " + transcribedText);
                execute(message);
            } else {
                log.error("Ошибка сервера: {}", response.body());
                sendErrorMessage(update, "Ошибка обработки голоса");
            }
        } catch (Exception e) {
            log.error("Ошибка обработки голосового сообщения", e);
            sendErrorMessage(update, "Техническая ошибка");
        }
    }

    private void sendErrorMessage(Update update, String errorMessage) {
        try {
            SendMessage message = SendMessage.builder()
                    .chatId(update.getMessage().getChatId().toString())
                    .text(errorMessage)
                    .build();
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Failed to send error message", e);
        }
    }

    private void handleContactUpdate(Update update, RegistrationContext registrationContext) throws TelegramApiException, JsonProcessingException {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        registrationContext.setTelephone(userId, update.getMessage().getContact().getPhoneNumber());

        SendMessage sm = SendMessage.builder()
                .chatId(chatId.toString())
                .text("✅ Верификация успешна.\n" +
                        "Пожалуйста заполните анкету:")
                .build();
        execute(sm);
        commandsHandler.mapStatusToHandler(update, registrationContext.getStatus(userId), userId, registrationContext);
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

    @Override
    public void deleteMessage(String chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);
        try {
            this.execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void editMessageText(String chatId, Integer messageId, String newText, InlineKeyboardMarkup keyboard) throws TelegramApiException {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(newText);
        editMessage.setReplyMarkup(keyboard);
        execute(editMessage);
    }


    private SendMessage requestPhoneNumber(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(Answers.PLEASE_GIVE_TELEPHONE_NUMBER.getMessage())
                .replyMarkup(verification.createContactRequestKeyboard())
                .build();
    }


}