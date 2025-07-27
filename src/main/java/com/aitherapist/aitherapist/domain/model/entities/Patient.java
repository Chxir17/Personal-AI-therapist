package com.aitherapist.aitherapist.domain.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("PATIENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Patient extends User {

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyHealthData> dailyHealthDataList = new ArrayList<>();
    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    private InitialHealthData initialData;
    public void editInitialData(DailyHealthData dailyHealthData, Long healthDataId) {}

    public void editDailyHealthData(DailyHealthData dailyHealthData, Long healthDataId) {
        dailyHealthDataList.stream()
                .filter(hd -> Objects.equals(hd.getId(), healthDataId))
                .findFirst()
                .ifPresentOrElse(
                        existingHd -> {
                            BeanUtils.copyProperties(dailyHealthData, existingHd, "id", "patient");
                            existingHd.setPatient(this);
                        },
                        () -> {
                            dailyHealthData.setPatient(this);
                            dailyHealthDataList.add(dailyHealthData);
                        }
                );
    }

    public void addDailyHealthData(DailyHealthData dailyHealthData) {
        dailyHealthData.setPatient(this);
        dailyHealthDataList.add(dailyHealthData);
    }

    public Map<String, String> buildGoalsInformation(){
        Map<String, String> goals = new HashMap<>();
        goals.put("pressure", "120");
        goals.put("pulse", "70");
        return goals;
    }

    public void removeHealthData(Long healthDataId) {
        dailyHealthDataList.removeIf(hd -> Objects.equals(hd.getId(), healthDataId));
    }

    @Transactional(readOnly = true)
    public Map<String, String> buildMedicalHistory() {
        var result = new LinkedHashMap<String, String>();

        List<DailyHealthData> history = this.getDailyHealthDataList();

        result.put("bloodOxygenLevel", makeDataList(
                history.stream().map(DailyHealthData::getBloodOxygenLevel).toList()
        ));

        result.put("temperature", makeDataList(
                history.stream().map(DailyHealthData::getTemperature).toList()
        ));

        result.put("hoursOfSleepToday", makeDataList(
                history.stream().map(DailyHealthData::getHoursOfSleepToday).toList()
        ));

        result.put("pulse", makeDataList(
                history.stream().map(DailyHealthData::getPulse).toList()
        ));

        result.put("pressure", makeDataList(
                history.stream().map(DailyHealthData::getPressure).toList()
        ));
        return result;
    }
    @Override
    public String toString() {
        String parentToString = super.toString().replaceFirst("User\\{", "Patient{");

        return parentToString +
                ", dailyHealthDataCount=" + (dailyHealthDataList != null ? dailyHealthDataList.size() : 0) +
                ", initialDataId=" + (initialData != null ? initialData.getId() : "null") +
                '}';
    }
}
