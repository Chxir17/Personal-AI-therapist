package com.aitherapist.aitherapist.services.interfaces;

import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;

import java.util.List;

public interface IPatientService {
    void editPatient(Patient patient);
    Patient findById(Long id);
    Patient findByName(String name);
    void deleteById(Long id);
    void editPatientDailyHealthData(Patient patient, DailyHealthData dailyHealthData);
    void deletePatientDailyHealthData(Patient patient);
    List<DailyHealthData> getPatientDailyHealthData(Long patientId);
    boolean hasDailyHealthData(Long patientId);
    void editPatientDailyHealthData(Long patientId, DailyHealthData dailyHealthData);
    void addPatientDailyHealthData(Long id, DailyHealthData dailyHealthData);
    void setInitialHealthDataToUser(Long userId, InitialHealthData initialHealthData);
    InitialHealthData getInitialDailyHealthData(Long userId);
    void deleteInitialDailyHealthData(Long userId);
    void addDailyHealthDataToPatient(Long patientId, DailyHealthData dailyHealthData);
}