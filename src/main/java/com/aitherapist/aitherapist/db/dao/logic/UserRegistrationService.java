package com.aitherapist.aitherapist.db.dao.logic;

import com.aitherapist.aitherapist.db.dao.services.UserServiceImpl;
import com.aitherapist.aitherapist.db.entities.User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * UserRegistrationService - class without http request work.
 * ==DataController but without http.
 */
@Service
public class UserRegistrationService {
    private final UserServiceImpl userService;

    @Autowired
    public UserRegistrationService(UserServiceImpl userService) {
        this.userService = userService;
    }

    public void registerUser(Integer userId, User user) {
        userService.createUser(userId, user);
    }

}