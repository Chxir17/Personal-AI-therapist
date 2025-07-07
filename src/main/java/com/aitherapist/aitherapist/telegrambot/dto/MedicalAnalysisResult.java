package com.aitherapist.aitherapist.telegrambot.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.List;

/**
 * MedicalAnalysisResult - object for transfer and
 */
@Data
@Getter
@Setter
public class MedicalAnalysisResult {
    private boolean isMedical;
    private JSONObject jsonObject;

    @Override
    public String toString() {
        return "isMedical -  " + (isMedical ? "yes":"no") + " medical analysis: " + jsonObject;
    }
}