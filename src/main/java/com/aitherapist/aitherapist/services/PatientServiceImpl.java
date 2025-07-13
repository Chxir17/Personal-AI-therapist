package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.domain.model.entities.HealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.domain.model.entities.UserActivityLog;
import com.aitherapist.aitherapist.repositories.IPatient;
import com.aitherapist.aitherapist.services.interfaces.IPatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PatientServiceImpl implements IPatientService {

    private final IPatient patientRepository;

    @Autowired
    public PatientServiceImpl(IPatient patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    @Transactional
    public void editPatient(Patient patient) {
        Patient existingPatient = patientRepository.findById(patient.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        existingPatient.setName(patient.getName());
        existingPatient.setAge(patient.getAge());
        existingPatient.setGender(patient.getGender());
        existingPatient.setPhoneNumber(patient.getPhoneNumber());

        patientRepository.save(existingPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Patient findByName(String name) {
        return patientRepository.findByName(name);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        patientRepository.delete(patient);
    }

    @Override
    @Transactional
    public void editPatientHealthData(Patient patient, HealthData healthData) {
        Patient existingPatient = patientRepository.findById(patient.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        existingPatient.editHealthData(healthData, healthData.getId());
        patientRepository.save(existingPatient);
    }

    @Override
    @Transactional
    public void deletePatientHealthData(Patient patient) {
        Patient existingPatient = patientRepository.findById(patient.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        existingPatient.getHealthDataList().clear();
        patientRepository.save(existingPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HealthData> getPatientHealthData(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return patient.getHealthDataList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasHealthData(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return !patient.getHealthDataList().isEmpty();
    }


}