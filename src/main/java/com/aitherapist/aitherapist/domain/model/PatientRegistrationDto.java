package com.aitherapist.aitherapist.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientRegistrationDto {
    private String user_type;
    private String name;
    private LocalDate birthDate;
    private Boolean gender;
    private String chronicDiseases;
    private Double height;
    private Double weight;
    private String badHabits;
    private Long clinicId; // обязательное поле для ClinicPatient
    private String medicalCardNumber;
}