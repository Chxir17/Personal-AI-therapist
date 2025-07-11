package com.aitherapist.aitherapist.services;

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
    public Doctor getDoctor(int DoctorId) {
        return doctorRepository.findById(DoctorId).get();
    }

    @Override
    public List<Patient> getPatients(int DoctorId) {
        Doctor doctor = getDoctor(DoctorId);
        return new ArrayList<>(doctor.getPatients());
    }

    @Override
    public Patient getPatientById(int DoctorId, int userId) {
        Doctor doctor = getDoctor(DoctorId);
        return doctor.getPatients().get(userId);
    }

    @Override
    public void updatePatient(int userId, Patient patient) {
        Doctor doctor = getDoctor(userId);
        doctorRepository.editPatient(doctor.getId(), patient);
    }


    @Override
    public void deletePatient(int DoctorId, Integer id) {
        Doctor doctor = getDoctor(DoctorId);
        doctor.getPatients().remove(getPatientById(id));
    }

    @Override
    public void createPatient(int DoctorId, Patient patient) {
        Doctor doctor = getDoctor(DoctorId);
        doctorRepository.createNewPatient(doctor.getId(), patient);
    }

    @Override
    public void editPatient(int DoctorId, Patient patient) {
        Doctor doctor = getDoctor(DoctorId);
        doctorRepository.editPatient(doctor.getId(), patient);
    }

    @Override
    public void deleteAllPatients(int DoctorId) {
        Doctor doctor = getDoctor(DoctorId);
        doctorRepository.deleteAllPatients(doctor.getId());
    }

    @Override
    public void updateUserHealthData(int DoctorId, Integer userId, HealthData healthData) {
        Doctor doctor = getDoctor(DoctorId);
        Patient patient1 = doctorRepository.getPatientById(userId);
        patient1.editHealthData(healthData, healthData.getId());
    }

    @Override
    public void createUserHealthData(int DoctorId, Integer userId, HealthData healthData) {
        Doctor doctor = getDoctor(DoctorId);
        Patient patient1 = doctorRepository.getPatientById(userId);
        patient1.editHealthData(healthData, healthData.getId());
    }

    @Override
    public void deleteUserHealthData(int DoctorId, Integer userId) {
        Doctor doctor = getDoctor(DoctorId);
        Patient patient1 = doctorRepository.getPatientById(userId);
        patient1.removeHealthData(doctor.getId());
    }

    @Override
    public List<HealthData>  getUserHealthData(int DoctorId, Integer userId) {
        Doctor doctor = getDoctor(DoctorId);
        return doctorRepository.getPatientById(userId).getHealthDataList();

    }

}
