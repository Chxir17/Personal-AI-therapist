package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.repositories.IHealthDataRepository;
import com.aitherapist.aitherapist.repositories.IUserRepository;
import com.aitherapist.aitherapist.domain.model.entities.HealthData;
import com.aitherapist.aitherapist.domain.model.entities.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * HealthDataServiceImpl - provide main operation for db
 * IHealthDataRepository -  repository-interface. auto create interface with implemented main methods.
 */
@Service
@Transactional(readOnly=true)
public class HealthDataServiceImpl implements IHealthDataService {

    @Autowired
    private IHealthDataRepository healthDataRepository;

    @Autowired
    private IUserRepository userRepository;

    @Override
    @Transactional
    public HealthData saveHealthDataInUser(int userId, HealthData healthData) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found!" + userId));
        healthData.setUser(user);
        return healthDataRepository.save(healthData);

    }

    @Override
    @Transactional
    public List<HealthData> fetchHealthDataList(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return healthDataRepository.findByUser(user);
    }

    /**
     * findById, copyProperties - function from spring.
     * replace multi set.properties.
     * @param healthData
     * @param userId
     * @return
     */
    @Override
    @Transactional
    public HealthData updateHealthData(HealthData healthData, Integer userId) {
        HealthData currentHealthData = healthDataRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BeanUtils.copyProperties(healthData, currentHealthData, "id"); // ignore id
        return healthDataRepository.save(currentHealthData);
    }

    @Override
    @Transactional
    public void deleteHealthData(Integer userId) {
        HealthData healthData =  healthDataRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        healthDataRepository.delete(healthData);
    }
}
