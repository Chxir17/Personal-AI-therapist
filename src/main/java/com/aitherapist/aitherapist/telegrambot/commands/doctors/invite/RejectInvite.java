package com.aitherapist.aitherapist.telegrambot.commands.doctors.invite;

import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class RejectInvite implements ICommand {

    private final TelegramMessageSender telegramMessageSender;
    private final UserServiceImpl userService;

    @Autowired
    public RejectInvite(TelegramMessageSender telegramMessageSender,
                        UserServiceImpl userService) {
        this.telegramMessageSender = telegramMessageSender;
        this.userService = userService;
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        Long doctorId = TelegramIdUtils.extractUserId(update);
        Long chatId = TelegramIdUtils.getChatId(update);
        Long userId = TelegramIdUtils.extractUserId(update);

        if (update.hasCallbackQuery()) {
            String[] parts = update.getCallbackQuery().getData().split(" ");
            if (parts.length == 2) {
                Long patientId = Long.parseLong(parts[1]);

                User patient = userService.getUserByUserId(patientId);
                if (patient != null) {
                    SendMessage patientMessage = new SendMessage();
                    patientMessage.setChatId(patient.getTelegramId().toString());
                    patientMessage.setText("❌ Врач отклонил ваше приглашение");
                    telegramMessageSender.sendMessageAndSetToList(patientMessage, registrationContext, userId);
                }

                Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
                telegramExecutor.editMessageText(
                        chatId.toString(),
                        messageId,
                        "❌ Вы отклонили приглашение пациента",
                        null
                );
                return null;
            }
        }

        return new SendMessage(chatId.toString(), "❌ Не удалось обработать запрос");
    }}