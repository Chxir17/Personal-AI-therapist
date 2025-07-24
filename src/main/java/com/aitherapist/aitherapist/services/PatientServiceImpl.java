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
    @Transactional
    public void editPatient(Patient patient) {
        Patient existingPatient = patientRepository.findById(patient.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        existingPatient.setName(patient.getName());
        existingPatient.setBirthDate(patient.getBirthDate());
        existingPatient.setGender(patient.getGender());
        existingPatient.setPhoneNumber(patient.getPhoneNumber());

        patientRepository.save(existingPatient);
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
    public Patient findByName(String name) {
        return patientRepository.findByName(name);
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        patientRepository.delete(patient);
    }

    @Override
    @Transactional
    public void editPatientDailyHealthData(Patient patient, DailyHealthData dailyHealthData) {
        Patient existingPatient = patientRepository.findById(patient.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        existingPatient.editDailyHealthData(dailyHealthData, dailyHealthData.getId());
        patientRepository.save(existingPatient);
    }

    @Override
    @Transactional
    public void addPatientDailyHealthData(Long userId, DailyHealthData dailyHealthData) {
        Patient existingPatient = patientRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        existingPatient.editDailyHealthData(dailyHealthData, dailyHealthData.getId());
        patientRepository.save(existingPatient);
    }

    @Override
    @Transactional
    public void setInitialHealthDataToUser(Long userId, InitialHealthData initialHealthData) {
        Patient existingPatient = patientRepository.findByTelegramId(userId);
        existingPatient.setInitialData(initialHealthData);
    }

    @Override
    public InitialHealthData getInitialDailyHealthData(Long userId) {
        Patient existingPatient = patientRepository.findByTelegramId(userId);
        return existingPatient.getInitialData();
    }

    @Override
    public void deleteInitialDailyHealthData(Long userId) {
        Patient patient =  patientRepository.findByTelegramId(userId);
        patient.setInitialData(null);
    }


    @Override
    @Transactional
    public void editPatientDailyHealthData(Long patientId, DailyHealthData dailyHealthData) {
        Patient existingPatient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        existingPatient.editDailyHealthData(dailyHealthData, dailyHealthData.getId());
        patientRepository.save(existingPatient);
    }


    @Override
    @Transactional
    public void deletePatientDailyHealthData(Patient patient) {
        Patient existingPatient = patientRepository.findById(patient.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        existingPatient.getDailyHealthDataList().clear();
        patientRepository.save(existingPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyHealthData> getPatientDailyHealthData(Long patientId) {
        Patient patient = patientRepository.findByTelegramId(patientId);

        return patient.getDailyHealthDataList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasDailyHealthData(Long patientId) {
        Patient patient = patientRepository.findByTelegramId(patientId);

        return !patient.getDailyHealthDataList().isEmpty();
    }


}