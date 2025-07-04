package com.aitherapist.aitherapist.telegrambot.dto;

import lombok.Data;

import java.util.List;

@Data
public class MedicalAnalysisResult {
    private boolean isMedical;
    private MedicalDetails details;

    @Data
    public static class MedicalDetails {
        private List<String> keywordsFound;
        private List<String> numbersFound;
    }

    public static MedicalAnalysisResult getDefault() {
        MedicalAnalysisResult result = new MedicalAnalysisResult();
        result.setMedical(false);
        result.setDetails(new MedicalDetails());
        return result;
    }
}