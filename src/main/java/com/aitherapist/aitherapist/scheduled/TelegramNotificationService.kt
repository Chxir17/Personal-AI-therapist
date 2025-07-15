//package com.aitherapist.aitherapist.telegrambot.scheduled
//
//import com.aitherapist.aitherapist.domain.enums.NotificationStatus
//import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender
//import jakarta.annotation.PostConstruct
//import jakarta.transaction.Transactional
//import org.quartz.*
//import org.slf4j.LoggerFactory
//import org.springframework.stereotype.Service
//import java.time.LocalDateTime
//import java.time.ZoneId
//import java.util.*
//
///**
// * TelegramNotificationService -
// */
//@Service
//open class TelegramNotificationService(
//    private val scheduler: Scheduler,
//    private val notificationRepository: IScheduledNotificationRepository,
//    private val telegramSender: TelegramMessageSender
//) {
//
//    private val logger = LoggerFactory.getLogger(javaClass)
//
//    @Transactional
//    open fun scheduleNotification(
//        telegramChatId: Long,
//        message: String,
//        triggerTime: LocalDateTime,
//        internalUserId: Long? = null
//    ): Long {
//        // check that input time in future.
//        require(triggerTime.isAfter(LocalDateTime.now())) {
//            "Trigger time must be in the future"
//        }
//
//        val notification = ScheduledNotification(
//            telegramChatId = telegramChatId,
//            message = message,
//            triggerTime = triggerTime,
//            internalUserId = internalUserId
//        )
//
//        return try {
//            val saved = notificationRepository.save(notification)
//            scheduleQuartzJob(saved) // create task
//            logger.info("Notification [${saved.id}] scheduled successfully")
//            saved.id
//        } catch (e: Exception) {
//            logger.error("Failed to schedule notification", e)
//            throw RuntimeException("Unable to schedule notification", e)
//        }
//    }
//
//    /**
//     * create job
//     */
//    private fun scheduleQuartzJob(notification: ScheduledNotification) {
//        val jobDetail = JobBuilder.newJob(TelegramNotificationJob::class.java)
//            .withIdentity("notification_job_${notification.id}", "telegram_notifications")
//            .usingJobData(JobDataMap(mapOf(
//                "telegramChatId" to notification.telegramChatId,
//                "message" to notification.message,
//                "notificationId" to notification.id
//            )))
//            .storeDurably()
//            .build()
//
//        val trigger = TriggerBuilder.newTrigger()
//            .withIdentity("notification_trigger_${notification.id}", "telegram_notifications")
//            .startAt(Date.from(notification.triggerTime.atZone(ZoneId.systemDefault()).toInstant())) // time to start
//            .forJob(jobDetail)
//            .build()
//
//        try {
//            scheduler.scheduleJob(jobDetail, trigger) // reg jobDetail in scheduler.
//        } catch (e: SchedulerException) {
//            logger.error("Failed to schedule Quartz job for notificationId=${notification.id}", e)
//            throw RuntimeException("Quartz scheduling failed", e)
//        }
//    }
//
//    @Transactional
//    open fun cancelNotification(notificationId: Long): Boolean {
//        return try {
//            val jobKey = JobKey.jobKey("notification_job_$notificationId", "telegram_notifications")
//            val deleted = scheduler.deleteJob(jobKey)
//
//            if (deleted) {
//                notificationRepository.findById(notificationId).ifPresent {
//                    it.status = NotificationStatus.FAILED
//                    notificationRepository.save(it)
//                    logger.info("Notification [$notificationId] cancelled and marked as FAILED")
//                }
//            } else {
//                logger.warn("Notification [$notificationId] not found in scheduler")
//            }
//
//            deleted
//        } catch (e: SchedulerException) {
//            logger.error("Failed to cancel notification [$notificationId]", e)
//            throw RuntimeException("Cancel notification failed", e)
//        }
//    }
//
//    @PostConstruct
//    open fun restorePendingNotifications() {
//        try {
//            val now = LocalDateTime.now()
//            val pending = notificationRepository.findByStatusAndTriggerTimeAfter(NotificationStatus.PENDING, now)
//
//            pending.forEach {
//                try {
//                    scheduleQuartzJob(it)
//                    logger.info("Restored pending notification: ${it.id}")
//                } catch (e: Exception) {
//                    logger.error("Failed to restore notification: ${it.id}", e)
//                }
//            }
//
//        } catch (e: Exception) {
//            logger.error("Error while restoring pending notifications", e)
//        }
//    }
//}
