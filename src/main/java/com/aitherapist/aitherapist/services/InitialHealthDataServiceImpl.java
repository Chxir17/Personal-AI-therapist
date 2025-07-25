package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.repositories.IInitialHealthDataRepository;
import com.aitherapist.aitherapist.repositories.IPatientRepository;
import com.aitherapist.aitherapist.services.interfaces.IInitialHealthDataService;
import com.aitherapist.aitherapist.services.interfaces.IPatientService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InitialHealthDataServiceImpl implements IInitialHealthDataService {

    IInitialHealthDataRepository iInitialHealthDataRepository;
    IPatientRepository iPatientRepository;

    @Autowired
    public InitialHealthDataServiceImpl(IInitialHealthDataRepository iInitialHealthDataRepository, IPatientRepository iPatientRepository) {
        this.iInitialHealthDataRepository = iInitialHealthDataRepository;
        this.iPatientRepository = iPatientRepository;
    }

    @Transactional
    @Override
    public InitialHealthData getInitialHealthDataByUserId(Long userId) {
        Patient patient = iPatientRepository.findByTelegramId(userId);
        return patient.getInitialData();
    }

    @Transactional
    @Override
    public void updateInitialHealthDataByUserId(Long userId, InitialHealthData initialHealthData) {
        Patient patient = iPatientRepository.findByTelegramId(userId);
        BeanUtils.copyProperties(patient, initialHealthData, "id"); // ignore id, чтобы
        iPatientRepository.save(patient);
    }
}
