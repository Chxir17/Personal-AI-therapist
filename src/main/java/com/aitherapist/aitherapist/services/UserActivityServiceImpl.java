package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.domain.model.entities.UserActivityLog;
import com.aitherapist.aitherapist.repositories.IUserActivityLogRepository;
import com.aitherapist.aitherapist.services.interfaces.IUserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserActivityServiceImpl implements IUserActivityService {
    private final IUserActivityLogRepository activityLogRepository;

    @Autowired
    public UserActivityServiceImpl(IUserActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    @Transactional
    public void setUserMessage(String message, User user, Long activityId) {
        updateActivityLogMessage(user, activityId, message);
    }

    @Override
    @Transactional
    public void setUserTime(LocalDateTime time, User user, Long activityId) {
        updateActivityLogTime(user, activityId, time);
    }

    @Override
    @Transactional
    public void setPatientMessage(String message, Patient patient, Long activityId) {
        updateActivityLogMessage(patient, activityId, message);
    }

    @Override
    @Transactional
    public void setPatientTime(LocalDateTime time, Patient patient, Long activityId) {
        updateActivityLogTime(patient, activityId, time);
    }

    @Override
    @Transactional
    public void setDoctorMessage(String message, Doctor doctor, Long activityId) {
        updateActivityLogMessage(doctor, activityId, message);
    }

    @Override
    @Transactional
    public void setDoctorTime(LocalDateTime time, Doctor doctor, Long activityId) {
        updateActivityLogTime(doctor, activityId, time);
    }

    private void updateActivityLogMessage(User user, Long activityId, String message) {
        getActivityLog(user, activityId).ifPresent(log -> {
            log.setActionType(message);
            activityLogRepository.save(log);
        });
    }

    private void updateActivityLogTime(User user, Long activityId, LocalDateTime time) {
        getActivityLog(user, activityId).ifPresent(log -> {
            log.setActionTime(time);
            activityLogRepository.save(log);
        });
    }

    private Optional<UserActivityLog> getActivityLog(User user, Long activityId) {
        return user.getActivityLogs().stream()
                .filter(log -> log.getId().equals(activityId))
                .findFirst();
    }

    @Override
    @Transactional
    public void editUserActivity(User user, Long userId, UserActivityLog userActivityLog) {
        updateActivityLog(user, userId, userActivityLog);
    }

    @Override
    @Transactional
    public void editUserActivity(Patient patient, Long userId, UserActivityLog userActivityLog) {
        updateActivityLog(patient, userId, userActivityLog);
    }

    @Override
    @Transactional
    public void editUserActivity(Doctor doctor, Long userId, UserActivityLog userActivityLog) {
        updateActivityLog(doctor, userId, userActivityLog);
    }

    private void updateActivityLog(User user, Long userId, UserActivityLog updatedLog) {
        user.getActivityLogs().stream()
                .filter(log -> log.getId().equals(userId))
                .findFirst()
                .ifPresent(log -> {
                    log.setActionTime(updatedLog.getActionTime());
                    log.setActionType(updatedLog.getActionType());
                    log.setActionType(updatedLog.getActionType());
                    activityLogRepository.save(log);
                });
    }

    @Override
    public String getMessageByUserId(Long userId) {
        return activityLogRepository.findById(userId)
                .map(UserActivityLog::getActionType)
                .orElse("");
    }
}