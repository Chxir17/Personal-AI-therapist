package com.aitherapist.aitherapist.telegrambot.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * MedicalAnalysisResult - object for transfer and
 */
@Data
@Getter
@Setter
public class MedicalAnalysisResult {
    private boolean isMedical;
    private List<String> details;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Medical result: ").append(isMedical ? "Medical" : "Non-medical");
        if (details != null && !details.isEmpty()) {
            sb.append("\nDetails:");
            details.forEach(detail -> sb.append("\n- ").append(detail));
        }
        return sb.toString();
    }
}