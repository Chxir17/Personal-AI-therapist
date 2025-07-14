package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.domain.model.entities.NotificationConfig;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.repositories.INotificationRepository;
import com.aitherapist.aitherapist.repositories.IUserActivityLogRepository;
import com.aitherapist.aitherapist.services.interfaces.INotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@Transactional
public class NotificationServiceImpl implements INotificationService {
    private final INotificationRepository  notificationRepository;
    @Autowired
    public NotificationServiceImpl(IUserActivityLogRepository userActivityLog, INotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void setMessage(User user, String message) {
        NotificationConfig notificationConfig = notificationRepository
                .findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        notificationConfig.setCustomMessage(message);
    }

    @Override
    public void setNotificationTime(User user, LocalTime time) {
        NotificationConfig notificationConfig = notificationRepository
                .findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        notificationConfig.setNotificationUserTime(time);
    }

    @Override
    public void setNotificationEnabled(User user, boolean enabled) {
        NotificationConfig notificationConfig = notificationRepository
                .findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        notificationConfig.setNotificationsEnabled(enabled);
    }

    @Override
    public Boolean getNotificationEnabled(User user) {
        NotificationConfig notificationConfig = notificationRepository
                .findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        return notificationConfig.isNotificationsEnabled();
    }

    @Override
    public String getMessage(User user) {
        NotificationConfig notificationConfig = notificationRepository
                .findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        return notificationConfig.getCustomMessage();
    }

    @Override
    public LocalTime getNotificationTime(User user) {
        NotificationConfig notificationConfig = notificationRepository
                .findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        return notificationConfig.getNotificationUserTime();
    }
}
