package com.aitherapist.aitherapist.domain.model.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

/**
 * MedicalNormalData - keep for all clinic patient theirs normal data (check file @MedicalAnnalize)
 */

@Getter
@Setter
public class MedicalNormalData {
    public Long userId;
    public Double hoursOfSleepToday;
    public Long pulse;
    public String pressure;

    public MedicalNormalData(Long userId,  Double hoursOfSleepToday, Long pulse, String pressure) {
        this.userId = userId;
        this.hoursOfSleepToday = hoursOfSleepToday;
        this.pulse = pulse;
        this.pressure = pressure;
    }


    public String formatMedicalNormsForTelegram() {
        return String.format(
                "<b>🩺 Медицинские нормативы для вас</b>\n" +
                        "💤 <b>Рекомендуемый сон:</b> %.1f часов/сутки\n" +
                        "❤️ <b>Нормальный пульс:</b> %d уд/мин\n" +
                        "🩸 <b>Целевое давление:</b> %s\n\n" +
                        "📅 <b>Последнее обновление:</b> %s",
                getHoursOfSleepToday(),
                getPulse(),
                getPressure(),
                java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        );
    }


}
