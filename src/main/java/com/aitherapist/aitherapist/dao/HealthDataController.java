package com.aitherapist.aitherapist.dao;

import com.aitherapist.aitherapist.db.entities.HealthData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HealthDataController{
    private final IHealthDataRepository healthDataRepository;

    public String getUserHealthStatus(Integer id) {
        Optional<HealthData> healthData = healthDataRepository.findById(id);
        return healthData
                .map(data -> data.toString())
                .orElse("No data");
    }
}
