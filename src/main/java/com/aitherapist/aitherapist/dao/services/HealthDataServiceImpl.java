package com.aitherapist.aitherapist.dao.services;

import com.aitherapist.aitherapist.dao.repositorys.IHealthDataRepository;
import com.aitherapist.aitherapist.db.entities.HealthData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * HealthDataServiceImpl - provide main operation for db
 * IHealthDataRepository -  repository-interface. auto create interface with implemented main methods.
 */
@Service
public class HealthDataServiceImpl implements IHealthDataService {

    @Autowired
    private IHealthDataRepository healthDataRepository;

    @Override
    public HealthData saveHealthData(HealthData healthData) {
        return healthDataRepository.save(healthData);
    }



    @Override
    public List<HealthData> fetchHealhDataList(Integer userId) {
        return healthDataRepository.findAll();
    }

    /**
     * findById, copyProperties - function from spring.
     * replace multi set.properties.
     * @param healthData
     * @param userId
     * @return
     */
    @Override
    public HealthData updateHealthData(HealthData healthData, Integer userId) {
        HealthData currentHealthData = healthDataRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BeanUtils.copyProperties(healthData, currentHealthData, "id"); // ignore id
        return healthDataRepository.save(currentHealthData);
    }

    @Override
    public void deleteHealthData(Integer userId) {
        HealthData healthData =  healthDataRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        healthDataRepository.delete(healthData);
    }
}
