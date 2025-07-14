package com.aitherapist.aitherapist.repositories.sber;

import com.aitherapist.aitherapist.domain.model.sber.SDoctor;
import com.aitherapist.aitherapist.domain.model.sber.SPatient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISPatientRepository extends JpaRepository<SPatient, Long> {
    SPatient getSPatientsById(Long id);
}
