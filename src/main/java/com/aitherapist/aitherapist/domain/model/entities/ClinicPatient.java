package com.aitherapist.aitherapist.domain.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("CLINIC_PATIENT")
public class ClinicPatient extends Patient {

    @Column(name = "clinic_id", nullable = false)
    private Long clinicId;

    @Column(name = "medical_card_number")
    private String medicalCardNumber;

    @ManyToMany(mappedBy = "patients")
    private List<Doctor> doctors = new ArrayList<>();

    public Map<String, String> buildGoalsInformation(){
        Map<String, String> goals = new HashMap<>();
        goals.put("pressure", "120");
        goals.put("pulse", "70");
        return goals;
    }



    @Override
    public String toString() {
        return "ClinicPatient{" +
                super.toString() +
                ", clinicId=" + clinicId +
                ", medicalCardNumber='" + medicalCardNumber + '\'' +
                '}';
    }

}
