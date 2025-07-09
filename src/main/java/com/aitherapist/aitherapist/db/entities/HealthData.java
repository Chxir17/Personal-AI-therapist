package com.aitherapist.aitherapist.db.entities;

import lombok.*;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;


/**
 * HealthData - custom Object for Hibernate
 * example how to create
 * User = User.builder()
 *     .name("John Doe")
 *     .age(30)
 *     .build();
 * HealthData data = HealthData.builder()
 *     .user(user)
 *     .pulse(75)
 *     .temperature(36.6)
 *     .build();
 *
 * user.getHealthDataList().add(data);
 * userRepository.save(user);
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "health_data")
public class HealthData implements Serializable {
    @Serial
    private static final long serialVersionUID = -5527566248002296042L;

    @Id
    @Column(name = "id")
    @NonNull
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "blood_oxygen_level", columnDefinition = "DECIMAL(5,2)")
    private Double bloodOxygenLevel;

    @Column(name = "temperature", columnDefinition = "DECIMAL(3,1)")
    private Double temperature;

    @Column(name = "hours_of_sleep_today", columnDefinition = "DECIMAL(4,1)")
    private Double hoursOfSleepToday;

    @Column(name = "pulse")
    private Integer pulse;

    @Column(name = "pressure", columnDefinition = "DECIMAL(5,2)")
    private Double pressure;

    @Column(name = "sugar", columnDefinition = "DECIMAL(4,2)")
    private Double sugar;

    @Column(name = "heart_pain")
    private Boolean heartPain;

    @Column(name = "arrhythmia")
    private Boolean arrhythmia;

    @Override
    public String toString() {
        return "HealthData{" +
                "id=" + id +
                ", user=" + (user != null ? user.getId() : "null") +
                ", bloodOxygenLevel=" + bloodOxygenLevel +
                ", temperature=" + temperature +
                ", hoursOfSleepToday=" + hoursOfSleepToday +
                ", pulse=" + pulse +
                ", pressure=" + pressure +
                ", sugar=" + sugar +
                ", heartPain=" + heartPain +
                ", arrhythmia=" + arrhythmia +
                '}';
    }
}
