package com.aitherapist.aitherapist.domain.model.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MedicalNormalData - keep for all clinic patient theirs normal data (check file @MedicalAnnalize)
 */
@Getter
@Setter
public class MedicalNormalData {
    private Long userId;
    private Double hoursOfSleepToday;
    private Long pulse;
    private String pressure;
    private String lastUpdate;

    public MedicalNormalData(Long userId, Double hoursOfSleepToday, Long pulse, String pressure) {
        this.userId = userId;
        this.hoursOfSleepToday = hoursOfSleepToday;
        this.pulse = pulse;
        this.pressure = pressure;
        this.lastUpdate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }


}
