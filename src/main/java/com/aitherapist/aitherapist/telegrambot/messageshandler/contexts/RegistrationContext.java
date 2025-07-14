package com.aitherapist.aitherapist.telegrambot.messageshandler.contexts;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RegistrationContext {
    private final Map<Long, Status> registrationInProgress = new ConcurrentHashMap<>();

    /**
     * Need initianal information. next promt is main
     * @param chatId
     */
    public void startRegistration(long chatId) {
        registrationInProgress.put(chatId, Status.STARTED);
    }

    public boolean isRegistrationInProgress(long chatId) {
        return registrationInProgress.getOrDefault(chatId, Status.NONE) == Status.STARTED;
    }

    public Status getStatus(long chatId) {
        return registrationInProgress.get(chatId);
    }

    public boolean isVerify(Long id) {
        return getStatus(id) == Status.VERIFIED;
    }

    public void setVerify(Long userId, Status status) {
        registrationInProgress.put(userId, status);
    }


    public void completeRegistration(long chatId) {
        registrationInProgress.remove(chatId);
    }

    public void deleteRegistration(long chatId) {
        registrationInProgress.remove(chatId);
    }

    public boolean isContain(Long id) {
        return registrationInProgress.containsKey(id);
    }
}