package com.aitherapist.aitherapist.domain.model.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * MedicalAnalysisResult - object for transfer and
 */
@Data
@Getter
@Setter
public class MedicalAnalysisResult {
    private boolean isMedical;
    private HealthData healthData;

    @Override
    public String toString() {
        return "isMedical -  " + (isMedical ? "yes":"no") + " medical analysis: " + healthData.toString();
    }
}