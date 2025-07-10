package com.aitherapist.aitherapist.scheduled
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

//class SendMessageJob : Job {
//    override fun execute(context: JobExecutionContext) {
//        val dataMap = context.jobDetail.jobDataMap
//        val bot = dataMap["bot"] as MyTelegramBot
//        val chatId = dataMap["chatId"] as String
//        val messageText = dataMap["messageText"] as String
//        bot.execute(SendMessage(chatId, messageText))
//    }
//}