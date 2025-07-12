package com.aitherapist.aitherapist.domain.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("PATIENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Patient extends User {

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthData> healthDataList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserActivityLog> activityLogs = new ArrayList<>();

    public void editHealthData(HealthData healthData, Long healthDataId) {
        healthDataList.stream()
                .filter(hd -> Objects.equals(hd.getId(), healthDataId))
                .findFirst()
                .ifPresentOrElse(
                        existingHd -> {
                            BeanUtils.copyProperties(healthData, existingHd, "id", "patient");
                            existingHd.setPatient(this);
                        },
                        () -> {
                            healthData.setPatient(this);
                            healthDataList.add(healthData);
                        }
                );
    }

    public void removeHealthData(Long healthDataId) {
        healthDataList.removeIf(hd -> Objects.equals(hd.getId(), healthDataId));
    }
}
