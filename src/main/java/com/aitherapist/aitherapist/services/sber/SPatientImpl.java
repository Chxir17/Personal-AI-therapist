package com.aitherapist.aitherapist.services.sber;

import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.sber.SDoctor;
import com.aitherapist.aitherapist.domain.model.sber.SPatient;
import com.aitherapist.aitherapist.repositories.sber.ISDoctorRepository;
import com.aitherapist.aitherapist.repositories.sber.ISPatientRepository;
import com.aitherapist.aitherapist.services.interfaces.sber.ISPatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
public class SPatientImpl implements ISPatientService {

    private final ISPatientRepository patientRepository;
    @Autowired
    public SPatientImpl(ISPatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public String getFullName(Long doctorId) {
        SPatient patient = patientRepository.getSPatientsById(doctorId);
        return patient.getFullName();
    }

    @Override
    public String phoneNumber(Long doctorId) {
        SPatient patient = patientRepository.getSPatientsById(doctorId);
        return patient.getPhoneNumber();
    }

    @Override
    public String snilsNumber(Long doctorId) {
        SPatient patient = patientRepository.getSPatientsById(doctorId);
        return patient.getSnils();
    }

    @Override
    public String getCommentGoal(Long doctorId) {
        SPatient patient = patientRepository.getSPatientsById(doctorId);
        return patient.getCommentGoal();
    }
}
