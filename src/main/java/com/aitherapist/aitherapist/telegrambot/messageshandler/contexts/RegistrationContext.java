package com.aitherapist.aitherapist.telegrambot.messageshandler.contexts;

import com.aitherapist.aitherapist.domain.enums.DynamicStatus;
import com.aitherapist.aitherapist.domain.enums.Status;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RegistrationContext {
    private final Map<Long, DynamicStatus> mapOfUserStatus = new ConcurrentHashMap<>();
    private final Map<Long, DoctorRegistrationState> doctorRegistrationStates = new ConcurrentHashMap<>();

    public DoctorRegistrationState getDoctorRegistrationState(Long userId) {
        return doctorRegistrationStates.computeIfAbsent(userId, k -> new DoctorRegistrationState());
    }

    public void clearDoctorRegistrationState(Long userId) {
        doctorRegistrationStates.remove(userId);
    }

    public boolean hasDoctorRegistrationState(Long userId) {
        return doctorRegistrationStates.containsKey(userId);
    }


    public void startRegistration(long chatId) {
        mapOfUserStatus.put(chatId, Status.FIRST_PART_REGISTRATION_DOCTOR.withId(null));
    }

    public void start(long chatId) {
        mapOfUserStatus.put(chatId, Status.ALREADY_REGISTER.withId(null));
    }

    public boolean isRegistrationInProgress(long chatId) {
        return getDynamicStatus(chatId).is(Status.FIRST_PART_REGISTRATION_DOCTOR);
    }

    public Status getStatus(long userId) {
        return getDynamicStatus(userId).getBaseStatus();
    }

    public DynamicStatus getDynamicStatus(long userId) {
        return mapOfUserStatus.getOrDefault(userId, Status.NONE.withId(null));
    }

    public boolean isVerify(Long id) {
        return getDynamicStatus(id).is(Status.VERIFIED);
    }

    public void setVerify(Long userId, Status status) {
        mapOfUserStatus.put(userId, status.withId(null));
    }

    public void setStatus(Long userId, Status status) {
        mapOfUserStatus.put(userId, status.withId(null));
    }

    public void setStatusWithId(Long userId, Status status, Long associatedId) {
        mapOfUserStatus.put(userId, status.withId(associatedId));
    }

    public void completeRegistration(long chatId) {
        mapOfUserStatus.remove(chatId);
    }

    public void deleteRegistration(long chatId) {
        mapOfUserStatus.remove(chatId);
    }

    public boolean isContain(Long id) {
        return mapOfUserStatus.containsKey(id);
    }

    public boolean isStatusWithId(Long userId, Status status) {
        DynamicStatus ds = getDynamicStatus(userId);
        return ds.is(status) && ds.getAssociatedId() != null;
    }

    public Optional<Long> getAssociatedIdForStatus(Long userId, Status status) {
        DynamicStatus ds = getDynamicStatus(userId);
        if (ds.is(status)) {
            return Optional.ofNullable(ds.getAssociatedId());
        }
        return Optional.empty();
    }

    public void clearAssociatedId(Long userId) {
        DynamicStatus current = getDynamicStatus(userId);
        if (current.getAssociatedId() != null) {
            mapOfUserStatus.put(userId, current.getBaseStatus().withId(null));
        }
    }

    public List<Long> findUserIdsWithSendToUserStatus(Long doctorId) {
        List<Long> userIds = new ArrayList<>();

        for (Map.Entry<Long, DynamicStatus> entry : mapOfUserStatus.entrySet()) {
            DynamicStatus status = entry.getValue();
            if (status.is(Status.SEND_TO_THIS_USER) &&
                    doctorId.equals(status.getAssociatedId())) {
                userIds.add(entry.getKey());
            }
        }
        return userIds;
    }
}