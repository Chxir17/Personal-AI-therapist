//package com.aitherapist.aitherapist.scheduled.notifications.algorithm;
//
//import com.aitherapist.aitherapist.services.activity.UserActivityRegistrationService;
//import com.aitherapist.aitherapist.domain.model.entities.UserActivityLog;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.time.DayOfWeek;
//import java.time.LocalTime;
//import java.util.List;
//import java.util.Map;
//
////@Component
////public class GetUserNotificationTime {
////    @Autowired
////    private UserActivityRegistrationService timeLogs;
////
////    public Map<DayOfWeek, LocalTime> getTimeForNotificationById(long userID) {
////        List<UserActivityLog> logs = timeLogs.getByUserId(userID);
////        Map<DayOfWeek, LocalTime> timeToDayMap = NotificationTimeAnalyzer.getBestNotificationTimesForUser(logs);
////        return timeToDayMap;
////    }
////}