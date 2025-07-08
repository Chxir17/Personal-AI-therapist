package com.aitherapist.aitherapist.db.dao;

import com.aitherapist.aitherapist.db.dao.logic.UserRegistrationService;
import com.aitherapist.aitherapist.db.dao.services.HealthDataServiceImpl;
import com.aitherapist.aitherapist.db.dao.services.UserServiceImpl;
import com.aitherapist.aitherapist.db.entities.HealthData;
import com.aitherapist.aitherapist.db.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * FIXME: replace return smt to enum code errors or success
 * DataController - work with Services.
 * service - provide methods for database (implements repository
 * that extends JpaRepository<T, id>. T - generic entities for database.
 * JpaRepository - default java interface with methods(CRUD, ...) and add methods for Hibernate.
 * (for works with database. aka.dao)
 */
@Slf4j
@RestController()
@RequestMapping("/bot")
public class DataController {
    @Autowired
    private HealthDataServiceImpl healthDataService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRegistrationService userRegistrationService;

    @RequestMapping(path = "/user/{id}", method = RequestMethod.GET)
    public Boolean isSignUp(@PathVariable Integer userId) { //accepts a json object and
        if(userService.fetchUserList(userId).isEmpty()){
            return false;
        }
        return true;
    }

    /**
     * POST /bot/user/123/health
     * Content-Type: application/json
     *
     * {
     *     "bloodOxygenLevel": 98.5,
     *     "temperature": 36.6,
     *     "hoursOfSleepToday": 7.5,
     *     "pulse": 72,
     *     "pressure": 120.80,
     *     "sugar": 5.2,
     *     "heartPain": false,
     *     "arrhythmia": false
     * }
     *
     * check POST request with signature in @PostMapping.
     * @param userId
     * @param healthData auto parse json file and save in HealthData
     * @return
     */
    @PostMapping("/user/{userId}/health")
    public String saveUserHealthData(@PathVariable Integer userId, @RequestBody HealthData healthData) {
        healthDataService.saveHealthDataInUser(userId, healthData);
        return "data success save!";
    }

    /**
     * CreateAndSaveUserInformation - create user and save user information.
     * @param userId
     * @param user
     * @return
     */
    @PostMapping("/user/{userId}/information")
    public void CreateAndSaveUserInformation(@PathVariable Integer userId, @RequestBody User user) {
        userRegistrationService.registerUser(userId, user);
    }

}
