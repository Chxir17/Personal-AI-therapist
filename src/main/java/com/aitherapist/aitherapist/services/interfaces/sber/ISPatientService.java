package com.aitherapist.aitherapist.services.interfaces.sber;

public interface ISPatientService {
    String getFullName(Long doctorId);
    String phoneNumber(Long doctorId);
    String snilsNumber(Long doctorId);
    String getCommentGoal(Long doctorId);
}
