package com.aitherapist.aitherapist.domain.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("DOCTOR")
@Getter
@Setter
@NoArgsConstructor
public class Doctor extends User {
    @Column(name = "license_number", unique = true)
    private String licenseNumber;

    @ManyToMany
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

    public void removeAllPatients() {
        patients.clear();
    }

    public Patient getPatientById(Long patientId) {
        return patients.stream().filter(patient -> patient.getId().equals(patientId)).findFirst().orElse(null);
    }

    public List<HealthData> getUserHealthData(Long DoctorId, Long userId) {
        Patient patient = getPatientById(userId);
        return new ArrayList<>(patient.getHealthDataList());
    }
}