package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.entities.HealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.repositories.IDoctorRepository;
import com.aitherapist.aitherapist.services.interfaces.IDoctorService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class DoctorServiceImpl implements IDoctorService {
    private final IDoctorRepository doctorRepository;

    @Autowired
    public DoctorServiceImpl(IDoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }
    @Override
    public Doctor getDoctor(Long doctorId) {
        return doctorRepository.findById(doctorId).get();
    }

    @Override
    public List<Patient> getPatients(Long doctorId) {
        Doctor doctor = getDoctor(doctorId);
        return new ArrayList<>(doctor.getPatients());
    }

    @Override
    public Patient getPatientById(Long doctorId, Long userId) {
        Doctor doctor = getDoctor(doctorId);
        return doctor.getPatients().get(userId);
    }

    @Override
    public void updatePatient(Long doctorId, Long userId, ClinicPatient patient) {
        Doctor doctor = doctorRepository.findById(doctorId).get();
        List<ClinicPatient> lst = doctor.getPatients();
        for (ClinicPatient p : lst) {
            if (p.getId() == userId) {
                lst.remove(p);
                lst.add(p);
            }
        }
    }


    @Override
    public void deletePatient(Long doctorId, ClinicPatient patient) {
       Doctor doctor = getDoctor(doctorId);
       doctor.removePatient(patient);
    }

    @Override
    public void createPatient(Long doctorId, ClinicPatient patient) {
        Doctor doctor = getDoctor(doctorId);
        doctor.addPatient(patient);
    }


    @Override
    public void deleteAllPatients(Long doctorId) {
        Doctor doctor = getDoctor(doctorId);
        doctor.removeAllPatients();
    }

    @Override
    public void updateUserHealthData(Long doctorId, Long userId, HealthData healthData) {
        Doctor doctor = getDoctor(doctorId);
        Patient patient = doctor.getPatientById(userId);
        patient.editHealthData(healthData, healthData.getId());
    }

    @Override
    public void createUserHealthData(Long doctorId, Long userId, HealthData healthData) {
        Doctor doctor = getDoctor(doctorId);
        Patient patient = doctor.getPatientById(userId);
        patient.editHealthData(healthData, healthData.getId());
    }

    @Override
    public void deleteUserHealthData(Long doctorId, Long userId, HealthData healthData) {
        Doctor doctor = getDoctor(doctorId);
        Patient patient = doctor.getPatientById(userId);
        patient.removeHealthData(healthData.getId());
    }

    @Override
    public List<HealthData> getUserHealthData(Long doctorId, Long userId) {
        Doctor doctor = getDoctor(doctorId);
        return doctor.getUserHealthData(doctorId, userId);
    }

}
