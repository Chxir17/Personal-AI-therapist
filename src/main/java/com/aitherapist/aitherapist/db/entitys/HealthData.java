package com.aitherapist.aitherapist.db.entitys;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class HealthData {
    public int id;
    public int userId;
    public double bloodOxygenLevel;
    public double temperature;
    public double hoursOfSleepToday;
    public int pulse;
    public double pressure;
    public double sugar;
    public String heartPain;
    public String arrhythmia;
}
