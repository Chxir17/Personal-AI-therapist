package com.aitherapist.aitherapist.services.interfaces;

import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.entities.HealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;

import java.util.List;

public interface IDoctorService {
    Doctor getDoctor(Long doctorId);
    List<Patient> getPatients(Long doctorId);
    Patient getPatientById(Long doctorId, Long userId);
    void updatePatient(Long doctorId, Long userId, ClinicPatient patient);
    void deletePatient(Long doctorId, ClinicPatient patient);
    ClinicPatient createPatient(Long doctorId, ClinicPatient patient);
    void deleteAllPatients(Long doctorId);
    HealthData updateUserHealthData(Long doctorId, Long userId, HealthData healthData);
    HealthData createUserHealthData(Long doctorId, Long userId, HealthData healthData);
    void deleteUserHealthData(Long doctorId, Long userId, Long healthDataId);
    List<HealthData> getUserHealthData(Long doctorId, Long userId);
}
