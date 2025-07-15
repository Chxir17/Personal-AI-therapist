package com.aitherapist.aitherapist.domain.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("PATIENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Patient extends User {

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthData> healthDataList = new ArrayList<>();

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

    public static <T> String makeDataList(List<T> values){
        StringBuilder sb = new StringBuilder();
        for (T val : values) {
            sb.append(val).append(", ");
        }
        if (!values.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    public Map<String, String> buildMedicalHistory() {
        var result = new LinkedHashMap<String, String>();

        List<HealthData> history = this.getHealthDataList();

        result.put("bloodOxygenLevel", makeDataList(
                history.stream().map(HealthData::getBloodOxygenLevel).toList()
        ));

        result.put("temperature", makeDataList(
                history.stream().map(HealthData::getTemperature).toList()
        ));

        result.put("hoursOfSleepToday", makeDataList(
                history.stream().map(HealthData::getHoursOfSleepToday).toList()
        ));

        result.put("pulse", makeDataList(
                history.stream().map(HealthData::getPulse).toList()
        ));

        result.put("pressure", makeDataList(
                history.stream().map(HealthData::getPressure).toList()
        ));


        result.put("heartPain", makeDataList(
                history.stream().map(HealthData::getHeartPain).toList()
        ));

        result.put("arrhythmia", makeDataList(
                history.stream().map(HealthData::getArrhythmia).toList()
        ));

        return result;
    }
}
