//package com.aitherapist.aitherapist.telegrambot.scheduled
//
//import com.aitherapist.aitherapist.telegrambot.utils.sender.TelegramMessageSender
//import org.quartz.Job
//import org.quartz.JobExecutionContext
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Component
//
//@Component
//class TelegramNotificationJob : Job {
//
//    @Autowired
//    private lateinit var telegramSender: TelegramMessageSender
//
//    @Autowired
//    private lateinit var notificationRepository: IScheduledNotificationRepository
//
//    override fun execute(context: JobExecutionContext) {
//        val dataMap = context.mergedJobDataMap
//        val chatId = dataMap.getLong("telegramChatId")
//        val message = dataMap.getString("message")
//        val notificationId = dataMap.getLong("notificationId")
//
//        try {
//            telegramSender.sendMessage(chatId, message)
//
//            notificationRepository.findById(notificationId).ifPresent { notification ->
//                notification.status = NotificationStatus.SENT
//                notificationRepository.save(notification)
//            }
//        } catch (e: Exception) {
//            notificationRepository.findById(notificationId).ifPresent { notification ->
//                notification.status = NotificationStatus.FAILED
//                notificationRepository.save(notification)
//            }
//        }
//    }
//}
