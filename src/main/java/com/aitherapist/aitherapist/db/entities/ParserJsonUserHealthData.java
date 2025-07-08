package com.aitherapist.aitherapist.db.entities;

public class ParserJsonUserHealthData {
    private Double bloodOxygenLevel;
    private Double temperature;
    private Double hoursOfSleepToday;
    private Integer pulse;
    private Double pressure;
    private Double sugar;
    private Boolean heartPain;
    private Boolean arrhythmia;

    // Getters and setters
    public Double getBloodOxygenLevel() { return bloodOxygenLevel; }
    public void setBloodOxygenLevel(Double bloodOxygenLevel) { this.bloodOxygenLevel = bloodOxygenLevel; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getHoursOfSleepToday() { return hoursOfSleepToday; }
    public void setHoursOfSleepToday(Double hoursOfSleepToday) { this.hoursOfSleepToday = hoursOfSleepToday; }

    public Integer getPulse() { return pulse; }
    public void setPulse(Integer pulse) { this.pulse = pulse; }

    public Double getPressure() { return pressure; }
    public void setPressure(Double pressure) { this.pressure = pressure; }

    public Double getSugar() { return sugar; }
    public void setSugar(Double sugar) { this.sugar = sugar; }

    public Boolean getHeartPain() { return heartPain; }
    public void setHeartPain(Boolean heartPain) { this.heartPain = heartPain; }

    public Boolean getArrhythmia() { return arrhythmia; }
    public void setArrhythmia(Boolean arrhythmia) { this.arrhythmia = arrhythmia; }
}