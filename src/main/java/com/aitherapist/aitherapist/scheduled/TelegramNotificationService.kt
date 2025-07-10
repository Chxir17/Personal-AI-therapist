package com.aitherapist.aitherapist.telegrambot.scheduled

import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.quartz.*
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
open class TelegramNotificationService(
    private val scheduler: Scheduler,
    private val notificationRepository: IScheduledNotificationRepository,
    private val telegramSender: TelegramMessageSender
) {

    @Transactional
    open fun scheduleNotification(
        telegramChatId: Long,
        message: String,
        triggerTime: LocalDateTime,
        internalUserId: Long? = null
    ): Long {
        val notification = ScheduledNotification(
            telegramChatId = telegramChatId,
            message = message,
            triggerTime = triggerTime,
            internalUserId = internalUserId
        )

        val savedNotification = notificationRepository.save(notification)
        createQuartzJob(savedNotification.id, telegramChatId, message, triggerTime)

        return savedNotification.id
    }

    private fun createQuartzJob(
        notificationId: Long,
        telegramChatId: Long,
        message: String,
        triggerTime: LocalDateTime
    ) {
        try {
            val jobDataMap = JobDataMap().apply {
                put("telegramChatId", telegramChatId)
                put("message", message)
                put("notificationId", notificationId)
            }

            val jobDetail = JobBuilder.newJob(TelegramNotificationJob::class.java)
                .withIdentity("notification_job_$notificationId", "telegram_notifications")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build()

            val trigger = TriggerBuilder.newTrigger()
                .withIdentity("notification_trigger_$notificationId", "telegram_notifications")
                .forJob(jobDetail)
                .startAt(Date.from(triggerTime.atZone(ZoneId.systemDefault()).toInstant()))
                .build()

            scheduler.scheduleJob(jobDetail, trigger)
        } catch (e: SchedulerException) {
            throw RuntimeException("Failed to schedule notification job", e)
        }
    }

    @Transactional
    open fun cancelNotification(notificationId: Long): Boolean {
        return try {
            val jobKey = JobKey.jobKey("notification_job_$notificationId", "telegram_notifications")
            val deleted = scheduler.deleteJob(jobKey)

            if (deleted) {
                notificationRepository.findById(notificationId).ifPresent { notification ->
                    notification.status = NotificationStatus.FAILED
                    notificationRepository.save(notification)
                }
            }
            deleted
        } catch (e: SchedulerException) {
            throw RuntimeException("Failed to cancel notification", e)
        }
    }

    @PostConstruct
    open fun restorePendingNotifications() {
        val pendingNotifications = notificationRepository.findByStatusAndTriggerTimeAfter(
            NotificationStatus.PENDING,
            LocalDateTime.now()
        )

        pendingNotifications.forEach { notification ->
            createQuartzJob(
                notification.id,
                notification.telegramChatId,
                notification.message,
                notification.triggerTime
            )
        }
    }
}
