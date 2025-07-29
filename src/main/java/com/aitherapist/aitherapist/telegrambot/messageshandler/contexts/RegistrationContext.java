package com.aitherapist.aitherapist.telegrambot.messageshandler.contexts;

import com.aitherapist.aitherapist.domain.enums.DynamicStatus;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.History;
import com.aitherapist.aitherapist.domain.model.entities.MedicalNormalData;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.model.ClientRegistrationState;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.model.DoctorRegistrationState;
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
    private final Map<Long, ClientRegistrationState> clientRegistrationStates = new ConcurrentHashMap<>();

    private final Map<Long, History> mapUserToHistory = new ConcurrentHashMap<>();
    private final Map<Long, History> mapQaToHistory = new ConcurrentHashMap<>();

    private final Map<Long, MedicalNormalData>  mapMedicalNormalData = new ConcurrentHashMap<>();


    public void deleteAllDataOfUser(Long userId) {
        mapOfUserStatus.remove(userId);
        doctorRegistrationStates.remove(userId);
        clientRegistrationStates.remove(userId);
        mapUserToHistory.remove(userId);
        mapQaToHistory.remove(userId);
    }

    public MedicalNormalData getMedicalNormalData(Long userId) {
        if (!mapMedicalNormalData.containsKey(userId)) {
            mapMedicalNormalData.put(userId, new MedicalNormalData(userId, 8.5, 80L, "120/80"));
        }
        return mapMedicalNormalData.get(userId);
    }

    public List<History> getUserHistory(long userId) {
        List<History> historyList = new ArrayList<>();
        historyList.add(mapUserToHistory.get(userId));
        historyList.add(mapQaToHistory.get(userId));
        return historyList;
    }

    public void addItemToHistory(Long userId, String item, Boolean role) {
        History history;
        if (role) {
            history = mapUserToHistory.get(userId);
        } else {
            history = mapQaToHistory.get(userId);
        }
        if (history == null) {
            history = new History();
            history.addData(item);
            history.setRole(role);
            mapUserToHistory.put(userId, history);
        }
        history.addData(item);
    }

    public void setTelephone(Long userId, String telephone) {
        DynamicStatus status = mapOfUserStatus.computeIfAbsent(
                userId,
                k -> new DynamicStatus(null, null)
        );

        status.setTelephone(telephone);
        System.out.println(status.toString());

    }

    public String getTelephone(Long userId) {
        DynamicStatus status = mapOfUserStatus.get(userId);
        return status == null ? null : status.getTelephone();
    }

    public void clearClientRegistrationState(Long userId) {
        clientRegistrationStates.remove(userId);
    }

    public ClientRegistrationState getClientRegistrationState(Long userId) {
        return clientRegistrationStates.computeIfAbsent(userId, k -> new ClientRegistrationState());
    }

    public ClientRegistrationState resetClientRegistrationState(Long userId) {
        clientRegistrationStates.remove(userId);
        ClientRegistrationState newState = new ClientRegistrationState();
        clientRegistrationStates.put(userId, newState);
        return newState;
    }

    public DoctorRegistrationState getDoctorRegistrationState(Long userId) {
        return doctorRegistrationStates.computeIfAbsent(userId, k -> new DoctorRegistrationState());
    }

    public void clearDoctorRegistrationState(Long userId) {
        doctorRegistrationStates.remove(userId);
    }


    public void startRegistration(long chatId) {
        mapOfUserStatus.put(chatId, Status.REGISTRATION.withId(null));
    }


    public void start(long chatId) {
        mapOfUserStatus.put(chatId, Status.ALREADY_REGISTER.withId(null));
    }


    public Status getStatus(long userId) {
        return getDynamicStatus(userId).getBaseStatus();
    }

    public DynamicStatus getDynamicStatus(long userId) {
        return mapOfUserStatus.getOrDefault(userId, Status.NONE.withId(null));
    }

    public boolean isVerify(Long id) {
        return getDynamicStatus(id).is(Status.REGISTRATION);
    }

    public void setStatus(Long userId, Status status) {
        mapOfUserStatus.put(userId, status.withId(null));
    }

    public void setStatusWithId(Long userId, Status status, Long associatedId) {
        mapOfUserStatus.put(userId, status.withId(associatedId));
    }

    public List<Long> findUserIdsWithSendToDoctorStatus(Long doctorId) {
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

    public List<Long> findDoctorIdsWithSendToUserStatus(Long doctorId) {
        List<Long> userIds = new ArrayList<>();
        for (Map.Entry<Long, DynamicStatus> entry : mapOfUserStatus.entrySet()) {
            DynamicStatus status = entry.getValue();
            if (status.is(Status.SEND_TO_THIS_DOCTOR) &&
                    doctorId.equals(status.getAssociatedId())) {
                userIds.add(entry.getKey());
            }
        }
        return userIds;
    }
}