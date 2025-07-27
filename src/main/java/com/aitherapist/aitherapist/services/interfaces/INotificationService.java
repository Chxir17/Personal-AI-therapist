package com.aitherapist.aitherapist.services.interfaces;

import com.aitherapist.aitherapist.domain.model.entities.User;
import java.time.LocalTime;

public interface INotificationService {
    void setMessage(User user, String message);
    void setNotificationTime(User user, LocalTime time);
    void setNotificationEnabled(User user, boolean enabled);
    String getMessage(User user);
    LocalTime getNotificationTime(User user);
    Boolean getNotificationEnabled(Long userId);
    Boolean getNotificationEnabled(User user);
}