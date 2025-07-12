package com.aitherapist.aitherapist.domain.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("CLINIC_PATIENT")
public class ClinicPatient extends Patient {

    @Column(name = "clinic_id", nullable = false)
    private Long clinicId;

    @Column(name = "medical_card_number")
    private String medicalCardNumber;

    @ManyToMany(mappedBy = "patients")
    private List<Doctor> doctors = new ArrayList<>();
}
