package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
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
    public DailyHealthData saveHealthDataInUser(Long userId, DailyHealthData dailyHealthData) {
        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        dailyHealthData = healthDataRepository.save(dailyHealthData);
        patient.getDailyHealthDataList().add(dailyHealthData);
        patientRepository.save(patient);
        return dailyHealthData;
    }

    @Override
    public List<DailyHealthData> fetchHealthDataList(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"))
                .getDailyHealthDataList();
    }

    @Override
    @Transactional
    public DailyHealthData updateHealthData(DailyHealthData dailyHealthData, Long userId) {
        DailyHealthData currentDailyHealthData = healthDataRepository.findById(dailyHealthData.getId())
                .orElseThrow(() -> new RuntimeException("Health data not found"));

        patientRepository.findById(userId)
                .filter(p -> p.getDailyHealthDataList().contains(currentDailyHealthData))
                .orElseThrow(() -> new RuntimeException("Invalid user-healthdata relation"));

        BeanUtils.copyProperties(dailyHealthData, currentDailyHealthData, "id");
        return healthDataRepository.save(currentDailyHealthData);
    }

    @Override
    @Transactional
    public void deleteHealthData(Long healthDataId) {
        DailyHealthData dailyHealthData = healthDataRepository.findById(healthDataId)
                .orElseThrow(() -> new RuntimeException("Health data not found"));
        healthDataRepository.delete(dailyHealthData);
    }
}