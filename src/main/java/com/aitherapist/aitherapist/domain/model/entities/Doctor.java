package com.aitherapist.aitherapist.domain.model.entities;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
//FIXME исправить анотации
public class Doctor extends User{
    private List<ClinicPatient> trackingUsers;
}
