package com.aitherapist.aitherapist.scheduled.notifications.algorithm;

import com.aitherapist.aitherapist.db.dao.logic.UserActivityRegistrationService;
import com.aitherapist.aitherapist.db.entities.UserActivityLog;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class GetUserNotificationTime {
    //FIXME добавить Autowired
    private UserActivityRegistrationService timeLogs;

    public Map<DayOfWeek, LocalTime> getTimeForNotificationById(long userID) {
        List<UserActivityLog> logs = timeLogs.getByUserId(userID);
        Map<DayOfWeek, LocalTime> timeToDayMap = NotificationTimeAnalyzer.getBestNotificationTimesForUser(logs);
        return timeToDayMap;
    }
}