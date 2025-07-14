package com.aitherapist.aitherapist.services.sber;

import com.aitherapist.aitherapist.domain.model.sber.SDoctor;
import com.aitherapist.aitherapist.repositories.sber.ISDoctorRepository;
import com.aitherapist.aitherapist.services.interfaces.sber.ISDoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
public class SDoctorImpl implements ISDoctorService {
    public ISDoctorRepository doctorRepository;
    @Autowired
    public  SDoctorImpl(ISDoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public String getFullName(Long doctorId) {
        return doctorRepository.findById(doctorId).get().getFullName();
    }

    @Override
    public SDoctor getDoctor(Long doctorId) {
        return doctorRepository.findById(doctorId).get();
    }

    @Override
    public String getPhoneNumber(Long doctorId) {
        return doctorRepository.findById(doctorId).get().getPhoneNumber();
    }

    @Override
    public String getLicenceNumber(Long doctorId) {
        return doctorRepository.findById(doctorId).get().getLicenseNumber();
    }
}
