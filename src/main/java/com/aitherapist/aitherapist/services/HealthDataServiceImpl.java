package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.repositories.IHealthDataRepository;
import com.aitherapist.aitherapist.repositories.IUserRepository;
import com.aitherapist.aitherapist.domain.model.entities.HealthData;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.services.interfaces.IHealthDataService;
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
    public HealthData saveHealthDataInUser(Long userId, HealthData healthData) {
        return null;
    }

    @Override
    public List<HealthData> fetchHealthDataList(Long userId) {
        User user = userRepository.findById(Math.toIntExact(userId))
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
    public HealthData updateHealthData(HealthData healthData, Long userId) {
        HealthData currentHealthData = healthDataRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
        BeanUtils.copyProperties(healthData, currentHealthData, "id"); // ignore id
        return healthDataRepository.save(currentHealthData);
    }

    @Override
    public void deleteHealthData(Long userId) {
        HealthData healthData =  healthDataRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
        healthDataRepository.delete(healthData);
    }
}
