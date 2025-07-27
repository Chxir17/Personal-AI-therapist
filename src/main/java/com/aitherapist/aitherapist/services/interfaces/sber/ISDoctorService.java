package com.aitherapist.aitherapist.services.interfaces.sber;

import com.aitherapist.aitherapist.domain.model.sber.SDoctor;

public interface ISDoctorService {
    String getFullName(Long doctorId);
    SDoctor getDoctor(Long doctorId);
    String getPhoneNumber(Long doctorId);
    String getLicenceNumber(Long doctorId);
}
