package com.aitherapist.aitherapist.repositories;

import com.aitherapist.aitherapist.domain.model.entities.NotificationConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface INotificationRepository extends JpaRepository<NotificationConfig, Long> {

}
