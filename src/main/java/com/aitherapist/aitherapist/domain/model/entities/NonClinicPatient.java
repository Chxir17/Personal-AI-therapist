package com.aitherapist.aitherapist.domain.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("NON_CLINIC_PATIENT")
public class NonClinicPatient extends Patient {

    @Column(name = "self_registered")
    private Boolean selfRegistered;

    @Column(name = "external_system_id")
    private Long externalSystemId;
}
