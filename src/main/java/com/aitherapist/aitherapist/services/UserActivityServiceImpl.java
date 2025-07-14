package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.domain.model.entities.UserActivityLog;
import com.aitherapist.aitherapist.repositories.IUserActivityLog;
import com.aitherapist.aitherapist.services.interfaces.IUserActivityServiceImpl;
import com.aitherapist.aitherapist.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class UserActivityServiceImpl implements IUserActivityServiceImpl {

    private final IUserActivityLog userActivityLog;
    private final IUserService userService;
    @Autowired
    public UserActivityServiceImpl(IUserActivityLog userActivityLog,  IUserService userService) {
        this.userActivityLog = userActivityLog;
        this.userService = userService;
    }

    @Override
    public void setUserMessage(String message, User user, Long activityId) {

    }

    @Override
    public void setUserTime(LocalDateTime time, User user, Long activityId) {

    }

    @Override
    public void setPatientMessage(String message, Patient user, Long activityId) {

    }

    @Override
    public void setPatientTime(LocalDateTime time, Patient user, Long activityId) {

    }

    @Override
    public void setDoctorMessage(String message, Doctor doctor, Long activityId) {

    }

    @Override
    public void setDoctorTime(LocalDateTime time, Doctor doctor, Long activityId) {

    }

    @Override
    public void editUserActivity(User user, Long userId, UserActivityLog userActivityLog) {

    }

    @Override
    public void editUserActivity(Patient user, Long userId, UserActivityLog userActivityLog) {

    }

    @Override
    public void editUserActivity(Doctor user, Long userId, UserActivityLog userActivityLog) {

    }

    @Override
    public String getMessageByUserId(Long userId) {
        return "";
    }
}
