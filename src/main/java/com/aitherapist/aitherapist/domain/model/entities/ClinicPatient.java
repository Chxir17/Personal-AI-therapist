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
public class ClinicPatient extends User {

    private Integer idInClinic;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserActivityLog> activityLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthData> healthDataList = new ArrayList<>();

    @ManyToMany(mappedBy = "patients")
    private List<Doctor> doctors = new ArrayList<>();
}