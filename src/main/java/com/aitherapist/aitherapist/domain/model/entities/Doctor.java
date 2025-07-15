package com.aitherapist.aitherapist.domain.model.entities;

import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import jakarta.persistence.*;
import lombok.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("DOCTOR")
public class Doctor extends User {

    @Column(name = "license_number", unique = true)
    private String licenseNumber;

    @ManyToMany
    @JoinTable(
            name = "doctor_patient",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id")
    )
    private List<ClinicPatient> patients = new ArrayList<>();

    public void addPatient(ClinicPatient patient) {
        patients.add(patient);
        patient.getDoctors().add(this);
    }

    public void removePatient(ClinicPatient patient) {
        patients.remove(patient);
        patient.getDoctors().remove(this);
    }

    public void removeAllPatients() {
        for (ClinicPatient patient : new ArrayList<>(patients)) {
            removePatient(patient);
        }
    }

    public ClinicPatient getPatientById(Long patientId) {
        return patients.stream().filter(p -> p.getId().equals(patientId)).findFirst().orElse(null);
    }

    public List<DailyHealthData> getUserHealthData(Long userId) {
        ClinicPatient patient = getPatientById(userId);
        return patient != null ? patient.getDailyHealthDataList() : new ArrayList<>();
    }

    public SendMessage showDoctorMenu(Long chatId) {
        InlineKeyboardMarkup commands = InlineKeyboardFactory.createDoctorDefaultKeyboard();

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text("Выберите команду")
                .replyMarkup(commands)
                .build();
    }
}
