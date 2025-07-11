package com.aitherapist.aitherapist.telegrambot.messageshandler.contexts;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RegistrationContext {
    private final Map<Long, Boolean> registrationInProgress = new ConcurrentHashMap<>();

    /**
     * Need initianal information. next promt is main
     * @param chatId
     */
    public void startRegistration(long chatId) {
        registrationInProgress.put(chatId, true);
    }

    public boolean isRegistrationInProgress(long chatId) {
        return registrationInProgress.getOrDefault(chatId, false);
    }

    public void completeRegistration(long chatId) {
        registrationInProgress.remove(chatId);
    }

    public void deleteRegistration(long chatId) {
        registrationInProgress.remove(chatId);
    }
}