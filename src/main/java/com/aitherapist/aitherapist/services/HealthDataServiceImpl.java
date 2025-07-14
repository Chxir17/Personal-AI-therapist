package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.domain.model.entities.HealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.repositories.IHealthDataRepository;
import com.aitherapist.aitherapist.repositories.IPatientRepository;
import com.aitherapist.aitherapist.services.interfaces.IHealthDataService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class HealthDataServiceImpl implements IHealthDataService {
    private final IPatientRepository patientRepository;
    private final IHealthDataRepository healthDataRepository;

    @Autowired
    public HealthDataServiceImpl(IPatientRepository patientRepository,
                                 IHealthDataRepository healthDataRepository) {
        this.patientRepository = patientRepository;
        this.healthDataRepository = healthDataRepository;
    }

    @Override
    @Transactional
    public HealthData saveHealthDataInUser(Long userId, HealthData healthData) {
        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        healthData = healthDataRepository.save(healthData);
        patient.getHealthDataList().add(healthData);
        patientRepository.save(patient);
        return healthData;
    }

    @Override
    public List<HealthData> fetchHealthDataList(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"))
                .getHealthDataList();
    }

    @Override
    @Transactional
    public HealthData updateHealthData(HealthData healthData, Long userId) {
        HealthData currentHealthData = healthDataRepository.findById(healthData.getId())
                .orElseThrow(() -> new RuntimeException("Health data not found"));

        patientRepository.findById(userId)
                .filter(p -> p.getHealthDataList().contains(currentHealthData))
                .orElseThrow(() -> new RuntimeException("Invalid user-healthdata relation"));

        BeanUtils.copyProperties(healthData, currentHealthData, "id");
        return healthDataRepository.save(currentHealthData);
    }

    @Override
    @Transactional
    public void deleteHealthData(Long healthDataId) {
        HealthData healthData = healthDataRepository.findById(healthDataId)
                .orElseThrow(() -> new RuntimeException("Health data not found"));
        healthDataRepository.delete(healthData);
    }
}