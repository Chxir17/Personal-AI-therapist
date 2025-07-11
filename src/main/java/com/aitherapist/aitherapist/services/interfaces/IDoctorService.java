package com.aitherapist.aitherapist.services.interfaces;

import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.entities.HealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;

import java.util.List;

public interface IDoctorService {
    Doctor getDoctor(int DoctorId);
    List<Patient> getPatients(int DoctorId);
    Patient getPatientById(int DoctorId, int userId);
    void updatePatient(int DoctorId, Patient patient);
    void deletePatient(int DoctorId, Integer userId);
    void createPatient(int DoctorId, Patient patient);
    void editPatient(int DoctorId, Patient patient);
    void deleteAllPatients(int DoctorId);
    void updateUserHealthData(int DoctorId, Integer userId, HealthData healthData);
    void createUserHealthData(int DoctorId, Integer userId, HealthData healthData);
    void deleteUserHealthData(int DoctorId, Integer userId);
    HealthData getUserHealthData(int DoctorId, Integer userId);

}
