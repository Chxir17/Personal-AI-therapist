package com.aitherapist.aitherapist.repositories;

import com.aitherapist.aitherapist.domain.model.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPatient extends JpaRepository<Patient, Long> {
}
