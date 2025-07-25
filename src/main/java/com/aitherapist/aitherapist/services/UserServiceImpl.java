package com.aitherapist.aitherapist.services;
import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.model.entities.*;
import com.aitherapist.aitherapist.repositories.IUserRepository;

import com.aitherapist.aitherapist.services.interfaces.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * UserServiceImpl - implements IUserService (provides main operation)
 * IUserRepository - repository-interface. auto create interface with implemented main methods.
 * @Transactional annotation helps in one transaction do work.
 */
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements IUserService {


    @Autowired
    private IUserRepository userRepository;

    @Override
    @Transactional
    public void registerUser(Long userId, User user) {
        saveUser(user);
    }

    @Override
    @Transactional
    public ClinicPatient getClinicPatientById(Long telegramId) {
        User user = userRepository.findByTelegramId(telegramId);
        if (user instanceof ClinicPatient) {
            return  (ClinicPatient) user;
        }
        return null;
    }

    @Override
    @Transactional
    public Boolean isSignUp(Long userId){
        return fetchUserByTelegramId(userId) != null;
    }

    @Override
    @Transactional
    public User getUserByUserId(Long userId){
        return getUser(userId);
    }


    @Override
    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User fetchUserByTelegramId(Long id) {
        return userRepository.findByTelegramId(id);
    }

    /**
     * findById, copyProperties - function from spring.
     * replace multi set.properties.
     *
     * @param user
     * @param id
     */
    @Override
    @Transactional
    public void updateUser(User user, Long id) {
        User currentUser = userRepository.findByTelegramId(id);
        BeanUtils.copyProperties(user, currentUser, "id"); // ignore id, чтобы
        userRepository.save(currentUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public void deleteUserIfExists(Long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.delete(userRepository.findById(id).get());
        }
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findByTelegramId(id);
    }

    @Override
    public Boolean checkIfUserExists(Long userId) {
        return checkIfUserExists(userId);
    }

    @Override
    public Roles getUserRoles(Long userId) {
        User user = fetchUserByTelegramId(userId);
        return user.getRole();
    }

    @Override
    public Boolean isUserInClinic(Long userId) {
        User user = fetchUserByTelegramId(userId);
       return user.getRole() == Roles.DOCTOR ||  user.getRole() == Roles.CLINIC_PATIENT;
    }

    @Override
    public void changeUserRoles(Long userId, Roles role) {
        User user = fetchUserByTelegramId(userId);
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public void editUserInformation(User user, Long id) {
        editUserInformation(user, id);
    }

    @Override
    public void editUserHealthData(User user, Long id, DailyHealthData dailyHealthData) {
        editUserHealthData(user, id, dailyHealthData);
    }

    @Override
    @Transactional
    public void addActivityLog(User user, String actionType, Long messageId) {
        UserActivityLog log = new UserActivityLog();
        log.setUser(user);
        log.setActionTime(LocalDateTime.now());
        log.setActionType(actionType);
        log.setMessageId(messageId);

        user.getActivityLogs().add(log);
        userRepository.save(user);
    }

    @Override
    public UserActivityLog getUserActivityLog(User user, Long id) {
        return user.getActivityLogs().stream().filter(activityLog -> activityLog.getId().equals(id)).findFirst().get();
    }

    @Override
    public void editActivityLog(User user, Long id, UserActivityLog userActivityLog) {
        List<UserActivityLog> userActivityLogs = user.getActivityLogs();
        for (UserActivityLog cur : userActivityLogs) {
            if (cur.getId().equals(id)) {
                userActivityLogs.remove(cur);
                userActivityLogs.add(userActivityLog);
                break;
            }
        }
    }
}

