package com.aitherapist.aitherapist.domain.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import jakarta.persistence.*;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "daily_health_data")
public class DailyHealthData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "blood_oxygen_level", columnDefinition = "DECIMAL(5,2)")
    private Double bloodOxygenLevel;

    @Column(name = "temperature", columnDefinition = "DECIMAL(3,1)")
    private Double temperature;

    @Column(name = "hours_of_sleep_today", columnDefinition = "DECIMAL(4,1)")
    private Double hoursOfSleepToday;

    @Column(name = "pulse")
    private Long pulse;

    @Column(name = "pressure", columnDefinition = "VARCHAR(10)")
    private String pressure;


    @Override
    public String toString() {
        return "dailyHealthData{" +
                "id=" + id +
                ", user=" + (patient != null ? patient.getId() : "null") +
                ", bloodOxygenLevel=" + bloodOxygenLevel +
                ", temperature=" + temperature +
                ", hoursOfSleepToday=" + hoursOfSleepToday +
                ", pulse=" + pulse +
                ", pressure=" + pressure +
                '}';
    }
}
