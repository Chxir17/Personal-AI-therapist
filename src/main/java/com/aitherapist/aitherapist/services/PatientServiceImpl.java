package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.repositories.IDailyHealthDataRepository;
import com.aitherapist.aitherapist.repositories.IPatientRepository;
import com.aitherapist.aitherapist.services.interfaces.IPatientService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PatientServiceImpl implements IPatientService {

    private final IPatientRepository patientRepository;
    private final IDailyHealthDataRepository dailyHealthDataRepository;
    @Autowired
    public PatientServiceImpl(IPatientRepository patientRepository, IDailyHealthDataRepository dailyHealthDataRepository) {
        this.patientRepository = patientRepository;
        this.dailyHealthDataRepository = dailyHealthDataRepository;
    }

    @Override
    @Transactional
    public void addDailyHealthDataToPatient(Long patientId, DailyHealthData dailyHealthData) {
        Patient patient = patientRepository.findByTelegramId(patientId);
        if (patient == null) {
            throw new RuntimeException("Пациент не найден с telegramId: " + patientId);
        }

        dailyHealthData.setPatient(patient);
        patient.addDailyHealthData(dailyHealthData);

        dailyHealthDataRepository.save(dailyHealthData);
        patientRepository.save(patient);
    }

    @Transactional
    public Patient getPatientWithData(Long id) {
        Patient patient = patientRepository.findByTelegramId(id);

        patient.getDailyHealthDataList().size();
        if (patient.getInitialData() != null) {
            patient.getInitialData().getHeight();
        }

        return patient;
    }


    @Override
    @Transactional(readOnly = true)
    public Patient findById(Long userId) {
        Patient patient = patientRepository.findByTelegramId(userId);
        if (patient != null) {
            Hibernate.initialize(patient.getDailyHealthDataList());
        }
        return patient;
    }


    @Override
    @Transactional(readOnly = true)
    public List<DailyHealthData> getPatientDailyHealthData(Long patientId) {
        Patient patient = patientRepository.findByTelegramId(patientId);

        return patient.getDailyHealthDataList();
    }


}