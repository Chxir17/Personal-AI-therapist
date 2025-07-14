package com.aitherapist.aitherapist.services.interfaces;

import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.domain.model.entities.UserActivityLog;

import java.time.LocalDateTime;

public interface IUserActivityServiceImpl {

    void setUserMessage(String message, User user, Long activityId);
    void setUserTime(LocalDateTime time, User user, Long activityId);
    void setPatientMessage(String message, Patient user, Long activityId);
    void setPatientTime(LocalDateTime time, Patient user, Long activityId);
    void setDoctorMessage(String message, Doctor doctor, Long activityId);
    void setDoctorTime(LocalDateTime time, Doctor doctor, Long activityId);
    void editUserActivity(User user, Long userId, UserActivityLog userActivityLog);
    void editUserActivity(Patient user, Long userId, UserActivityLog userActivityLog);
    void editUserActivity(Doctor user, Long userId, UserActivityLog userActivityLog);
    String getMessageByUserId(Long userId);

}
