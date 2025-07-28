package com.aitherapist.aitherapist.repositories;

import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.model.entities.Doctor;

import com.aitherapist.aitherapist.domain.model.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.management.relation.Role;
import java.util.List;
import java.util.Optional;

@Repository
public interface IDoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByTelegramId(Long userTelegramId);
    Doctor getByTelegramId(Long userTelegramId);
    List<Doctor> findAllByRole(Roles role);
    @Modifying
    @Query(value = "INSERT INTO doctor_patient (doctor_id, patient_id) VALUES (?1, ?2)", nativeQuery = true)
    void addPatientToDoctor(Long doctorId, Long patientId);
}