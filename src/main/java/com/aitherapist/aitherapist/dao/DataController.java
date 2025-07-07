package com.aitherapist.aitherapist.dao;

import com.aitherapist.aitherapist.dao.services.HealthDataServiceImpl;
import com.aitherapist.aitherapist.dao.services.UserServiceImpl;
import com.aitherapist.aitherapist.db.entities.HealthData;
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
@RestController("/bot")
public class DataController {
    @Autowired
    private HealthDataServiceImpl healthDataService;

    @Autowired
    private UserServiceImpl userService;

    /**
     * @param healthData
     * @return
     * @PostMapping("/health") - processing POST-request (/bot/health
     */
    @PostMapping("/health")
    public String saveHealthData(@RequestBody HealthData healthData) { //accepts a json object and
        // automatically converts it to healthdata
        healthDataService.saveHealthData(healthData);
        return "data success save!";
    }

    @RequestMapping(path = "/user/{id}", method = RequestMethod.GET)
    public Boolean isSignUp(@PathVariable Integer userId) { //accepts a json object and
        if(userService.fetchUserList(userId).isEmpty()){
            return false;
        }
        return true;
    }

//    @RequestMapping(path="/user/{id}", method = RequestMethod.GET)
//    public User getUserData(){
//        return userService.fetchUserList();
//    }

}
