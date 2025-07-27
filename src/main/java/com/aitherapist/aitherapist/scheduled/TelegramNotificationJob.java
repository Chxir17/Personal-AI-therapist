package com.aitherapist.aitherapist.scheduled;

import com.aitherapist.aitherapist.domain.enums.NotificationStatus;
import com.aitherapist.aitherapist.services.NotificationServiceImpl;
import com.aitherapist.aitherapist.scheduled.IScheduledNotificationRepository;
import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TelegramNotificationJob implements Job {
    @Autowired
    private TelegramMessageSender telegramSender;

    @Autowired
    private IScheduledNotificationRepository notificationRepository;

    @Autowired
    private NotificationServiceImpl notificationService;

    @Override
    public void execute(JobExecutionContext context) {
        var dataMap = context.getMergedJobDataMap();
        long chatId = dataMap.getLong("telegramChatId");
        String message = dataMap.getString("message");
        long notificationId = dataMap.getLong("notificationId");
        long userId = dataMap.getLong("userId");

        try {
            Boolean isEnabled = notificationService.getNotificationEnabled(userId);
            if (isEnabled != null && isEnabled) {
                telegramSender.sendMessage(chatId, message);
                notificationRepository.findById(notificationId).ifPresent(notification -> {
                    notification.setStatus(NotificationStatus.SENT);
                    notificationRepository.save(notification);
                });
            } else {
                notificationRepository.findById(notificationId).ifPresent(notification -> {
                    notification.setStatus(NotificationStatus.CANCELLED);
                    notificationRepository.save(notification);
                });
            }
        } catch (Exception e) {
            notificationRepository.findById(notificationId).ifPresent(notification -> {
                notification.setStatus(NotificationStatus.FAILED);
                notificationRepository.save(notification);
            });
        }
    }
}