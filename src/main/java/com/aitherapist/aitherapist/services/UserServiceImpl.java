package com.aitherapist.aitherapist.services;
import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.entities.HealthData;
import com.aitherapist.aitherapist.repositories.IUserRepository;
import com.aitherapist.aitherapist.domain.model.entities.User;

import com.aitherapist.aitherapist.services.interfaces.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserServiceImpl - implements IUserService (provides main operation)
 * IUserRepository - repository-interface. auto create interface with implemented main methods.
 * @Transactional annotation helps in one transaction do work.
 */
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements IUserService {

    @Autowired
    private HealthDataServiceImpl healthDataService;

    @Autowired
    private IUserRepository userRepository;

    public void registerUser(Long userId, User user) {
        createUser(userId, user);
    }

    public Boolean isSignUp(Long userId){
        User user = fetchUser(userId);
        return user != null;
    }

    public String saveUserHealthData(Long userId, HealthData healthData){
        healthDataService.saveHealthDataInUser(userId, healthData);
        return "data success save!";
    }

    public User getUserByUserId(Long userId){
        return getUser(userId);
    }

    public void putHealthDataInUser(Long userId, HealthData healthData){
        healthDataService.saveHealthDataInUser(userId, healthData);
    }


    @Override
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User fetchUser(Long id) {
        return userRepository.findById(Math.toIntExact(id)).orElse(null);
    }

    /**
     * findById, copyProperties - function from spring.
     * replace multi set.properties.
     * @param user
     * @param id
     * @return save in db and return User
     */
    @Override
    @Transactional
    public User updateUser(User user, Long id) {
        User currentUser = userRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("User not found"));
        BeanUtils.copyProperties(user, currentUser, "id"); // ignore id, чтобы
        return userRepository.save(currentUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(Math.toIntExact(id)).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void createUser(Long userId, User user) {
        user.setId(userId);
        userRepository.save(user);
    }

    @Override
    public void deleteUserIfExists(Long id) {
        if (userRepository.findById(Math.toIntExact(id)).isPresent()) {
            userRepository.delete(userRepository.findById(Math.toIntExact(id)).get());
        }
    }

    @Override
    public User getUser(Long id) {
        User user =  userRepository.findById(Math.toIntExact(id)).orElseThrow(() -> new RuntimeException("User not found"));
        return user;
    }

    @Override
    public Boolean checkIfUserExists(Long userId) {
        return checkIfUserExists(userId);
    }

    @Override
    public Roles getUserRoles(Long userId) {
        User user = fetchUser(userId);
        return user.getRole();
    }

    @Override
    public Boolean isUserInClinic(Long userId) {
        User user = fetchUser(userId);
       return user.getRole() == Roles.DOCTOR ||  user.getRole() == Roles.CLINIC_PATIENT;
    }

    @Override
    public void changeUserRoles(Long userId, Roles role) {
        User user = fetchUser(userId);
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public void editUserInformation(User user, Long id) {
        editUserInformation(user, id);
    }

    @Override
    public void editUserHealthData(User user, Long id, HealthData healthData) {
        editUserHealthData(user, id, healthData);
    }
}
