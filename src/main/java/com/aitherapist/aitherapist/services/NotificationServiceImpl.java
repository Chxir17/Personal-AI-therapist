
package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.domain.model.entities.NotificationConfig;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.repositories.INotificationRepository;
import com.aitherapist.aitherapist.services.interfaces.INotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Optional;

@Service
@Transactional
public class NotificationServiceImpl implements INotificationService {
    private final INotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(INotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    private NotificationConfig getOrCreateConfig(User user) {
        return notificationRepository.findByUser(user)
                .orElseGet(() -> {
                    NotificationConfig newConfig = new NotificationConfig();
                    newConfig.setUser(user);
                    newConfig.setNotificationsEnabled(true); // включено по умолчанию
                    return notificationRepository.save(newConfig);
                });
    }

    public NotificationConfig getNotificationConfig(User user) {
        return notificationRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Notification config not found"));
    }

    @Override
    public void setMessage(User user, String message) {
        NotificationConfig config = getOrCreateConfig(user);
        config.setCustomMessage(message);
    }

    @Override
    public void setNotificationTime(User user, LocalTime time) {
        NotificationConfig config = getOrCreateConfig(user);
        config.setNotificationUserTime(time);
    }

    @Override
    public void setNotificationEnabled(User user, boolean enabled) {
        NotificationConfig config = getOrCreateConfig(user);
        config.setNotificationsEnabled(enabled);
    }

    @Override
    public Boolean getNotificationEnabled(User user) {
        NotificationConfig config = getOrCreateConfig(user);
        return config.isNotificationsEnabled();
    }

    @Override
    public String getMessage(User user) {
        NotificationConfig config = getOrCreateConfig(user);
        return config.getCustomMessage();
    }

    @Override
    public LocalTime getNotificationTime(User user) {
        NotificationConfig config = getOrCreateConfig(user);
        return config.getNotificationUserTime();
    }
}