package com.aitherapist.aitherapist.medical;

import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.MedicalNormalData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;

/**
 * MedicalAnnalize - class that analyze normal pressure and etc...
 * (check @MedicalNormalData class)
 */
@Component
public class MedicalAnnalize {

    private final UserServiceImpl userServiceImpl;

    @Autowired
    public MedicalAnnalize(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }


    private String formatMedicalNormsForTelegram(ClinicPatient patient, MedicalNormalData norms) {
        return String.format(
                "<b>🩺 Медицинские нормативы для пациента</b>\n" +
                        "👤 <b>%s</b>\n\n" +
                        "💤 <b>Рекомендуемый сон:</b> %.1f часов/сутки\n" +
                        "❤️ <b>Нормальный пульс:</b> %d уд/мин\n" +
                        "🩸 <b>Целевое давление:</b> %s\n\n" +
                        "📅 <b>Последнее обновление:</b> %s",
                patient.getName(),
                norms.getHoursOfSleepToday(),
                norms.getPulse(),
                norms.getPressure(),
                java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        );
    }
}