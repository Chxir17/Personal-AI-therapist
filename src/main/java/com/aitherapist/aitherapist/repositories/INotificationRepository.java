package com.aitherapist.aitherapist.repositories;

import com.aitherapist.aitherapist.domain.model.entities.NotificationConfig;
import com.aitherapist.aitherapist.domain.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface INotificationRepository extends JpaRepository<NotificationConfig, Long> {
    Optional<NotificationConfig> findByUser(User user);
}
