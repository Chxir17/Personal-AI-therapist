package com.aitherapist.aitherapist.scheduled.notifications.algorithm;

import com.aitherapist.aitherapist.domain.model.entities.NotificationConfig;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.domain.model.entities.UserActivityLog;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;


@Component
public class GetUserNotificationTime {
    @Autowired
    private UserServiceImpl userServiceImpl;
    public LocalTime getTimeForNotificationById(long userID) {
        User user = userServiceImpl.getUserByUserId(userID);
        NotificationConfig notificationConfig = user.getNotificationConfig();
        boolean isEnabled = notificationConfig.isNotificationsEnabled();
        LocalTime notificationUserTime = notificationConfig.getNotificationUserTime();
        if (isEnabled && notificationUserTime != null) {
            return notificationConfig.getNotificationUserTime();
        } else if(isEnabled && notificationUserTime == null){
            return null;
        }
        else {
            return null;
        }
    }
}