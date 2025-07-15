package com.aitherapist.aitherapist.telegrambot.messageshandler.contexts;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RegistrationContext {
    private final Map<Long, Status> mapOfUserStatus = new ConcurrentHashMap<>();

    /**
     * Need initianal information. next promt is main
     * @param chatId
     */
    public void startRegistration(long chatId) {
        mapOfUserStatus.put(chatId, Status.REGISTRATION);
    }

    public boolean isRegistrationInProgress(long chatId) {
        return mapOfUserStatus.getOrDefault(chatId, Status.NONE) == Status.REGISTRATION;
    }

    public Status getStatus(long userId) {
        return mapOfUserStatus.get(userId);
    }

    public boolean isVerify(Long id) {
        return getStatus(id) == Status.VERIFIED;
    }

    public void setVerify(Long userId, Status status) {
        mapOfUserStatus.put(userId, status);
    }

    public void setStatus(Long userId, Status status){mapOfUserStatus.put(userId, status);}

    public void completeRegistration(long chatId) {
        mapOfUserStatus.remove(chatId);
    }

    public void deleteRegistration(long chatId) {
        mapOfUserStatus.remove(chatId);
    }

    public boolean isContain(Long id) {
        return mapOfUserStatus.containsKey(id);
    }
}