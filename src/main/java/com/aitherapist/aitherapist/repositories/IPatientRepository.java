package com.aitherapist.aitherapist.repositories;

import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPatientRepository extends JpaRepository<Patient, Long> {
    Patient findByName(String name);
    Patient findById(long id);
    Patient findByTelegramId(Long telegramId);
    Patient getByTelegramId(Long telegramId);
}
