package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.model.entities.*;
import com.aitherapist.aitherapist.repositories.IDoctorRepository;
import com.aitherapist.aitherapist.services.interfaces.IDoctorService;
import com.aitherapist.aitherapist.services.interfaces.IUserService;
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
    private final IUserService userService;



    @Autowired
    public DoctorServiceImpl(IDoctorRepository doctorRepository, IUserService userService) {
        this.doctorRepository = doctorRepository;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAllByRole(Roles.DOCTOR);
    }

    @Override
    @Transactional
    public void addPatientToDoctor(Long doctorId, Long patientId) {

        doctorRepository.addPatientToDoctor(doctorId, patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public Doctor getDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.getByTelegramId(doctorId);
        if (doctor != null) {
            doctor.getPatients().size();
        }
        return doctor;
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
    public String getDoctorName(Long doctorId) {
        Doctor doctor = doctorRepository.getByTelegramId(doctorId);
        return doctor.getName();
    }
}