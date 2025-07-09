package com.aitherapist.aitherapist.db.dao.logic;

import com.aitherapist.aitherapist.db.dao.services.HealthDataServiceImpl;
import com.aitherapist.aitherapist.db.dao.services.UserServiceImpl;
import com.aitherapist.aitherapist.db.entities.HealthData;
import com.aitherapist.aitherapist.db.entities.User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * UserRegistrationService - class without http request work.
 * ==DataController but without http.
 */
@Service
public class UserRegistrationService {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private HealthDataServiceImpl healthDataService;

    @Autowired
    public UserRegistrationService(UserServiceImpl userService) {
        this.userService = userService;
    }

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
}