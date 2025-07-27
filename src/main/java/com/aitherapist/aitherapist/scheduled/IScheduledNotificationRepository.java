package com.aitherapist.aitherapist.scheduled;

import com.aitherapist.aitherapist.domain.enums.NotificationStatus;
import com.aitherapist.aitherapist.scheduled.ScheduledNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IScheduledNotificationRepository extends JpaRepository<ScheduledNotification, Long> {
    List<ScheduledNotification> findByTelegramChatId(Long chatId);
    List<ScheduledNotification> findByStatusAndTriggerTimeAfter(NotificationStatus status, LocalDateTime triggerTime);
}