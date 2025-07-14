package com.aitherapist.aitherapist.services.interfaces;

import com.aitherapist.aitherapist.domain.model.entities.User;

import java.time.LocalTime;

public interface INotificationService {
    void setMessage(User user, String message);
    void setNotificationTime(User user, LocalTime time);
    void setNotificationEnabled(User user, boolean enabled);
    Boolean getNotificationEnabled(User user);
    String getMessage(User user);
    LocalTime getNotificationTime(User user);
}