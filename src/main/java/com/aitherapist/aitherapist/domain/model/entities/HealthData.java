package com.aitherapist.aitherapist.domain.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "health_data")
public class HealthData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "pressure", columnDefinition = "VARCHAR(10)")
    private String pressure;

    @Column(name = "sugar", columnDefinition = "DECIMAL(4,2)")
    private Double sugar;

    @Column(name = "heart_pain")
    private Boolean heartPain;

    @Column(name = "arrhythmia")
    private Boolean arrhythmia;

    @Column(name = "chronic_diseases")
    private String chronicDiseases;

    @Column(name = "height")
    private Double height;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "bad_habits")
    private String badHabits;

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
