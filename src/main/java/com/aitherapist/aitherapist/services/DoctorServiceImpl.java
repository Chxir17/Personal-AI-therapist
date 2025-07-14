package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.entities.HealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.repositories.IDoctorRepository;
import com.aitherapist.aitherapist.services.interfaces.IDoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DoctorServiceImpl implements IDoctorService {
    private final IDoctorRepository doctorRepository;

    @Autowired
    public DoctorServiceImpl(IDoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public Doctor getDoctor(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Доктор с ID " + doctorId + " не найден"));
    }

    @Override
    public List<Patient> getPatients(Long doctorId) {
        return new ArrayList<>(getDoctor(doctorId).getPatients());
    }

    @Override
    public Patient getPatientById(Long doctorId, Long userId) {
        return getDoctor(doctorId).getPatients().stream()
                .filter(p -> p.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Пациент с ID " + userId + " не найден"));
    }

    @Override
    @Transactional
    public void updatePatient(Long doctorId, Long userId, ClinicPatient patient) {
        Doctor doctor = getDoctor(doctorId);
        List<ClinicPatient> patients = doctor.getPatients().stream()
                .filter(p -> !p.getId().equals(userId))
                .collect(Collectors.toList());
        patients.add(patient);
        doctor.setPatients(patients);
        doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public void deletePatient(Long doctorId, ClinicPatient patient) {
        Doctor doctor = getDoctor(doctorId);
        doctor.removePatient(patient);
        doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public void createPatient(Long doctorId, ClinicPatient patient) {
        Doctor doctor = getDoctor(doctorId);
        doctor.addPatient(patient);
        doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public void deleteAllPatients(Long doctorId) {
        Doctor doctor = getDoctor(doctorId);
        doctor.removeAllPatients();
        doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public void updateUserHealthData(Long doctorId, Long userId, HealthData healthData) {
        Patient patient = getPatientById(doctorId, userId);
        patient.editHealthData(healthData, healthData.getId());
    }

    @Override
    @Transactional
    public void createUserHealthData(Long doctorId, Long userId, HealthData healthData) {
        Patient patient = getPatientById(doctorId, userId);
        patient.editHealthData(healthData, healthData.getId());
    }

    @Override
    @Transactional
    public void deleteUserHealthData(Long doctorId, Long userId, HealthData healthData) {
        Patient patient = getPatientById(doctorId, userId);
        patient.removeHealthData(healthData.getId());
    }

    @Override
    public List<HealthData> getUserHealthData(Long doctorId, Long userId) {
        return getPatientById(doctorId, userId).getHealthDataList();
    }
}