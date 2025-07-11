package com.aitherapist.aitherapist.domain.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Doctor - extends User with main information
 */
@Entity
@DiscriminatorValue("DOCTOR") // Doctor type in db.
@Getter
@Setter
@NoArgsConstructor
public class Doctor extends User {

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "doctor_patient",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id")
    )
    private List<ClinicPatient> patients = new ArrayList<>();
    public void addPatient(ClinicPatient patient) {
        patients.add(patient);
        patient.getDoctors().add(this);
    }

    public void removePatient(ClinicPatient patient) {
        patients.remove(patient);
        patient.getDoctors().remove(this);
    }
}