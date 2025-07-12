package com.aitherapist.aitherapist.services.interfaces;

import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.entities.HealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IPatientService {
    void editPatient(Patient patient);
    Patient findById(Long id);
    Patient findByName(String name);
    void deleteById(Long id);
    void editPatientHealthData(Patient patient, HealthData healthData);
    void deletePatientHealthData(Patient patient);
    List<HealthData> getPatientHealthData(Long patientId);
    boolean hasHealthData(Long patientId);
    void addActivityLog(Patient patient, String actionType, Long messageId);
}