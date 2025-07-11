package com.aitherapist.aitherapist.domain.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("NON_CLINIC_PATIENT")
@Getter
@Setter
@NoArgsConstructor
public class NonClinicPatient extends Patient {
    @Column(name = "self_registered")
    private Boolean selfRegistered;

    @Column(name = "external_system_id")
    private Long externalSystemId;
}