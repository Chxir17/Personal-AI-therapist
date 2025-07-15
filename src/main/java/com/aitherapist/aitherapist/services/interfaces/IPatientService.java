package com.aitherapist.aitherapist.services.interfaces;

import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;

import java.util.List;

public interface IPatientService {
    void editPatient(Patient patient);
    Patient findById(Long id);
    Patient findByName(String name);
    void deleteById(Long id);
    void editPatientHealthData(Patient patient, DailyHealthData dailyHealthData);
    void deletePatientHealthData(Patient patient);
    List<DailyHealthData> getPatientHealthData(Long patientId);
    boolean hasHealthData(Long patientId);
    void editPatientHealthData(Long patientId, DailyHealthData dailyHealthData);
    void addPatientHealthData(Long id, DailyHealthData dailyHealthData);
}