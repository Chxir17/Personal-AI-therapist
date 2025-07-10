package com.aitherapist.aitherapist.scheduled

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface IScheduledNotificationRepository : JpaRepository<ScheduledNotification, Long> {
    fun findByTelegramChatId(chatId: Long): List<ScheduledNotification>
    fun findByStatusAndTriggerTimeAfter(status: NotificationStatus, triggerTime: LocalDateTime): List<ScheduledNotification>
}