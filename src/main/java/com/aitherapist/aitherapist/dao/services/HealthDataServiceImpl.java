package com.aitherapist.aitherapist.dao.services;

import com.aitherapist.aitherapist.dao.repositorys.IHealthDataRepository;
import com.aitherapist.aitherapist.db.entities.HealthData;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HealthDataServiceImpl {
    private final IHealthDataRepository healthDataRepository;

    public HealthDataServiceImpl(IHealthDataRepository healthDataRepository) {
        this.healthDataRepository = healthDataRepository;
    }

    public String getUserHealthStatus(Integer id) {
        Optional<HealthData> healthData = healthDataRepository.findById(id);
        return healthData
                .map(data -> data.toString())
                .orElse("No data");
    }
}
