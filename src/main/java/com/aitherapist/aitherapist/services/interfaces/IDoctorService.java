package com.aitherapist.aitherapist.services.interfaces;

import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;

import java.util.List;

public interface IDoctorService {
    Doctor getDoctor(Long doctorId);
    List<Patient> getPatients(Long doctorId);
    Patient getPatientById(Long doctorId, Long userId);
    Doctor createDoctor(Long doctorId, Doctor doctor);
    String getDoctorName(Long doctorId);
    List<Doctor> getAllDoctors();
}
