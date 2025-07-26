package com.aitherapist.aitherapist.services.interfaces;

import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;

import java.util.List;

public interface IPatientService {
    Patient findById(Long id);
    List<DailyHealthData> getPatientDailyHealthData(Long patientId);
    void addDailyHealthDataToPatient(Long patientId, DailyHealthData dailyHealthData);
}