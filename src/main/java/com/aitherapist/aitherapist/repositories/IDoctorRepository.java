package com.aitherapist.aitherapist.repositories;

import com.aitherapist.aitherapist.domain.model.entities.Doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface IDoctorRepository extends JpaRepository<Doctor, Long> {
}