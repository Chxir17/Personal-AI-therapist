package com.aitherapist.aitherapist.domain.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CLINIC_PATIENT")
@Getter
@Setter
@NoArgsConstructor
public class ClinicPatient extends Patient {
    @Column(name = "clinic_id", nullable = false)
    private Long clinicId;

    @ManyToMany(mappedBy = "patients")
    private List<Doctor> doctors = new ArrayList<>();

    @Column(name = "medical_card_number")
    private String medicalCardNumber;

    @Override
    public String getName() {
        return super.getName();
    }
}