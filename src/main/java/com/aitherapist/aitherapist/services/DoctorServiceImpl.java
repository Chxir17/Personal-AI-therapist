package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.domain.model.entities.*;
import com.aitherapist.aitherapist.repositories.IDoctorRepository;
import com.aitherapist.aitherapist.services.interfaces.IDoctorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        return doctorRepository.getByTelegramId(doctorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> getPatients(Long doctorId) {
        Doctor doctor = doctorRepository.getByTelegramId(doctorId);
        return new ArrayList<>(doctor.getPatients());
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
    public void updatePatient(Long doctorId, Long patientId, ClinicPatient updatedPatient) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + doctorId));
        ClinicPatient existingPatient = doctor.getPatients().stream()
                .filter(p -> p.getId().equals(patientId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + patientId + " for doctor: " + doctorId));

        updatedPatient.setId(patientId);
        updatedPatient.getDoctors().addAll(existingPatient.getDoctors());
        updatedPatient.setDailyHealthDataList(existingPatient.getDailyHealthDataList());
        doctor.getPatients().remove(existingPatient);
        doctor.getPatients().add(updatedPatient);
        doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public void deletePatient(Long doctorId, ClinicPatient patient) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Доктор не найден"));
        boolean patientExists = doctor.getPatients().stream()
                .anyMatch(p -> p.getId().equals(patient.getId()));

        if (!patientExists) {
            throw new EntityNotFoundException("Пациент не найден у указанного доктора");
        }
        doctor.removePatient(patient);
    }

    @Override
    @Transactional
    public ClinicPatient createPatient(Long doctorId, ClinicPatient patient) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Доктор не найден"));

        if (patient.getId() != null && doctor.getPatients().stream()
                .anyMatch(p -> p.getId().equals(patient.getId()))) {
            throw new IllegalStateException("Пациент уже существует");
        }

        doctor.addPatient(patient);
        return patient;
    }

    @Override
    @Transactional
    public void deleteAllPatients(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Доктор не найден"));
        doctor.removeAllPatients();
    }

    @Override
    @Transactional
    public void deleteUserHealthData(Long doctorId, Long patientId, Long healthDataId) {
        ClinicPatient patient = (ClinicPatient) getPatientById(doctorId, patientId);
        patient.removeHealthData(healthDataId);
    }

    @Override
    public List<DailyHealthData> getUserHealthData(Long doctorId, Long userId) {
        return getPatientById(doctorId, userId).getDailyHealthDataList();
    }

    @Override
    @Transactional
    public Doctor createDoctor(Long userTelegramId, Doctor doctorInput) {
        Optional<Doctor> existingDoctor = doctorRepository.findByTelegramId(userTelegramId);

        if (existingDoctor.isPresent()) {
            Doctor existing = existingDoctor.get();
            existing.setName(doctorInput.getName());
            existing.setGender(doctorInput.getGender());
            existing.setBirthDate(doctorInput.getBirthDate());
            existing.setPhoneNumber(doctorInput.getPhoneNumber());
            existing.setLicenseNumber(doctorInput.getLicenseNumber());
            existing.setRole(doctorInput.getRole());
            existing.setUpdatedAt(LocalDateTime.now());

            return doctorRepository.save(existing);
        } else {
            doctorInput.setId(null);
            doctorInput.setTelegramId(userTelegramId);
            return doctorRepository.save(doctorInput);
        }
    }

    @Override
    public List<DailyHealthData> getDailyHealthData(Long id, Long userId, Long healthDataId) {
        Doctor doctor = doctorRepository.getByTelegramId(id);
        ClinicPatient clinicPatient = doctor.getPatientById(userId);
        return clinicPatient.getDailyHealthDataList();
    }
}