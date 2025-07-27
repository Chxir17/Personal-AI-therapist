package com.aitherapist.aitherapist.scheduled;

import com.aitherapist.aitherapist.domain.enums.NotificationStatus;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Service
public class TelegramNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(TelegramNotificationService.class);

    private final Scheduler scheduler;
    private final IScheduledNotificationRepository notificationRepository;
    private final TelegramMessageSender telegramSender;

    public TelegramNotificationService(Scheduler scheduler,
                                       IScheduledNotificationRepository notificationRepository,
                                       TelegramMessageSender telegramSender) {
        this.scheduler = scheduler;
        this.notificationRepository = notificationRepository;
        this.telegramSender = telegramSender;
    }

    @Transactional
    public Long scheduleNotification(Long telegramChatId, String message,
                                     LocalDateTime triggerTime, Long internalUserId) {
        if (!triggerTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Trigger time must be in the future");
        }

        ScheduledNotification notification = new ScheduledNotification();
        notification.setTelegramChatId(telegramChatId);
        notification.setMessage(message);
        notification.setTriggerTime(triggerTime);
        notification.setInternalUserId(internalUserId);

        try {
            ScheduledNotification saved = notificationRepository.save(notification);
            scheduleQuartzJob(saved);
            logger.info("Notification [{}] scheduled successfully", saved.getId());
            return saved.getId();
        } catch (Exception e) {
            logger.error("Failed to schedule notification", e);
            throw new RuntimeException("Unable to schedule notification", e);
        }
    }

    private void scheduleQuartzJob(ScheduledNotification notification) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(TelegramNotificationJob.class)
                .withIdentity("notification_job_" + notification.getId(), "telegram_notifications")
                .usingJobData(new JobDataMap(Map.of(
                        "telegramChatId", notification.getTelegramChatId(),
                        "message", notification.getMessage(),
                        "notificationId", notification.getId()
                )))
                .storeDurably()
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("notification_trigger_" + notification.getId(), "telegram_notifications")
                .startAt(Date.from(notification.getTriggerTime().atZone(ZoneId.systemDefault()).toInstant()))
                .forJob(jobDetail)
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    @Transactional
    public boolean cancelNotification(Long notificationId) {
        try {
            JobKey jobKey = new JobKey("notification_job_" + notificationId, "telegram_notifications");
            boolean deleted = scheduler.deleteJob(jobKey);

            if (deleted) {
                notificationRepository.findById(notificationId).ifPresent(notification -> {
                    notification.setStatus(NotificationStatus.FAILED);
                    notificationRepository.save(notification);
                    logger.info("Notification [{}] cancelled and marked as FAILED", notificationId);
                });
            } else {
                logger.warn("Notification [{}] not found in scheduler", notificationId);
            }

            return deleted;
        } catch (SchedulerException e) {
            logger.error("Failed to cancel notification [{}]", notificationId, e);
            throw new RuntimeException("Cancel notification failed", e);
        }
    }

    @PostConstruct
    public void restorePendingNotifications() {
        try {
            LocalDateTime now = LocalDateTime.now();
            notificationRepository.findByStatusAndTriggerTimeAfter(NotificationStatus.PENDING, now)
                    .forEach(notification -> {
                        try {
                            scheduleQuartzJob(notification);
                            logger.info("Restored pending notification: {}", notification.getId());
                        } catch (Exception e) {
                            logger.error("Failed to restore notification: {}", notification.getId(), e);
                        }
                    });
        } catch (Exception e) {
            logger.error("Error while restoring pending notifications", e);
        }
    }
}