//package com.aitherapist.aitherapist.telegrambot.scheduled
//
//import jakarta.persistence.*
//import java.time.LocalDateTime
//
//@Entity
//@Table(name = "scheduled_notifications")
//class ScheduledNotification(
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    val id: Long = 0,
//
//    @Column(name = "internal_user_id")
//    val internalUserId: Long? = null,
//
//    @Column(name = "telegram_chat_id", nullable = false)
//    val telegramChatId: Long,
//
//    @Column(nullable = false)
//    val message: String,
//
//    @Column(name = "trigger_time", nullable = false)
//    val triggerTime: LocalDateTime,
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    var status: NotificationStatus = NotificationStatus.PENDING,
//
//    @Column(name = "created_at", nullable = false)
//    val createdAt: LocalDateTime = LocalDateTime.now()
//)
