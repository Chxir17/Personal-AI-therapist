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
    public void editUserInformation(User user, Long id) {
        editUserInformation(user, id);
    }

    @Override
    public void editUserHealthData(User user, Long id, DailyHealthData dailyHealthData) {
        editUserHealthData(user, id, dailyHealthData);
    }

}

