package com.aitherapist.aitherapist.domain.enums;

public enum Status  {
    NONE,
    FIRST_PART_REGISTRATION_DOCTOR,
    SECOND_PART_REGISTRATION,
    WAITING_VERIFICATION,
    VERIFIED,
    NON_VERIFIED,
    GIVING_PATIENT_ID,
    REWRITE_PATIENT_PARAMETERS,
    EDIT_NAME,
    EDIT_BIRTH_DATE,
    EDIT_GENDER,
    EDIT_ARRHYTHMIA,
    EDIT_CHRONIC_DISEASES,
    EDIT_HEIGHT,
    EDIT_WEIGHT,
    EDIT_BAD_HABITS,
    ALREADY_REGISTER,
    WAIT_DOCTOR_WRITE_MESSAGE_TO_USER,
    SEND_TO_THIS_USER;

    public DynamicStatus withId(Long id) {
        return new DynamicStatus(this, id);
    }
}
