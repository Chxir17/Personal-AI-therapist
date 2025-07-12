package com.aitherapist.aitherapist.repositories;

import com.aitherapist.aitherapist.domain.model.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPatient extends JpaRepository<Patient, Long> {
    Patient findByName(String name);
}
