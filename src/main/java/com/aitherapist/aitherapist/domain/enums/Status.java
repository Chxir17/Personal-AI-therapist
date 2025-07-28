package com.aitherapist.aitherapist.domain.enums;

public enum Status  {
    NONE,

    PATIENT_IN_MAIN_MENU,

    WAIT_MESSAGE_FROM_AI,
    SEND_TO_AI,

    REGISTERED_DOCTOR,
    REGISTERED_CLINIC_PATIENT,
    REGISTERED_NO_CLINIC_PATIENT,
    REGISTRATION,
    REGISTRATION_DOCTOR,
    REGISTRATION_CLINIC_PATIENT,
    REGISTRATION_NO_CLINIC_PATIENT,
    SECOND_PART_REGISTRATION,
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
    WAIT_USER_WRITE_MESSAGE_TO_DOCTOR,
    WRITE_DAILY_DATA,
    NOTIFICATION_SETTINGS,
    SET_NOTIFICATION_TIME,
    SET_NOTIFICATION_MESSAGE,
    SEND_TO_THIS_USER,
    SEND_TO_THIS_DOCTOR,
    GIVING_PHONE_NUMBER,
    QAMode;


    public boolean isRegistered() {
        return this.name().startsWith("REGISTERED_");
    }

    public boolean isRegistrationProcess() {
        return this.name().startsWith("REGISTRATION") || this.name().startsWith("REGISTRATION_CLINIC_PATIENT") || this.name().startsWith("REGISTRATION_NO_CLINIC_PATIENT") || this.name().startsWith("REGISTERED_CLINIC_PATIENT");
    }

    public DynamicStatus withId(Long id) {
        return new DynamicStatus(this, id);
    }
}
