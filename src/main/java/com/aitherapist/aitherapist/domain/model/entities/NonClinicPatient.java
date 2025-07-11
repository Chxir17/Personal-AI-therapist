package com.aitherapist.aitherapist.domain.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * NonClinicPatient - extends class User with main information.
 * FIXME: mb delete all list in this class??
 */
@Entity
@DiscriminatorValue("NON_CLINIC_PATIENT")
@Getter
@Setter
@NoArgsConstructor
public class NonClinicPatient extends User implements IPatient {

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserActivityLog> activityLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthData> healthDataList = new ArrayList<>();

    @Override
    public List<HealthData> getHealthData() {
        return this.healthDataList;
    }
}