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
    private UserServiceImpl userService;

    @Autowired
    private HealthDataServiceImpl healthDataService;

    public void registerUser(Integer userId, User user) {
        userService.createUser(userId, user);
    }

    public Boolean isSignUp(Integer userId){
        User user = userService.fetchUser(userId);
        return user != null;
    }

    public String saveUserHealthData(Integer userId, HealthData healthData){
        healthDataService.saveHealthDataInUser(userId, healthData);
        return "data success save!";
    }

    public User getUserByUserId(Integer userId){
        return userService.getUser(userId);
    }

    public void putHealthDataInUser(Integer userId, HealthData healthData){
        healthDataService.saveHealthDataInUser(userId, healthData);
    }

    @Autowired
    private IUserRepository userRepository;

    @Override
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User fetchUser(Integer id) {
        return userRepository.findById(id).orElse(null);
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
    public User updateUser(User user, Integer id) {
        User currentUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BeanUtils.copyProperties(user, currentUser, "id"); // ignore id, чтобы
        return userRepository.save(currentUser);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void createUser(int userId, User user) {
        user.setId(userId);
        userRepository.save(user);
    }

    @Override
    public void deleteUserIfExists(Integer id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.delete(userRepository.findById(id).get());
        }
    }

    @Override
    public User getUser(Integer id) {
        User user =  userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return user;
    }

    @Override
    public Boolean checkIfUserExists(Integer userId) {
        return userService.checkIfUserExists(userId);
    }

    @Override
    public Roles getUserRoles(Integer userId) {
        User user = userService.fetchUser(userId);
        return user.getRole();
    }

    @Override
    public Boolean isUserInClinic(Integer userId) {
        User user = userService.fetchUser(userId);
       return user.getRole() == Roles.DOCTOR ||  user.getRole() == Roles.CLINIC_PATIENT;
    }

    @Override
    public void changeUserRoles(Integer userId, Roles role) {
        User user = userService.fetchUser(userId);
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public void editUserInformation(User user, int id) {
        userService.editUserInformation(user, id);
    }

    @Override
    public void editUserHealthData(User user, int id, HealthData healthData) {
        userService.editUserHealthData(user, id, healthData);
    }
}
