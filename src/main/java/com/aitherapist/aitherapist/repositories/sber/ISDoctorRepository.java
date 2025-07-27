package com.aitherapist.aitherapist.repositories.sber;

import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.sber.SDoctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISDoctorRepository extends JpaRepository<SDoctor, Long> {
    SDoctor getSDoctorById(Long doctorId);
}
