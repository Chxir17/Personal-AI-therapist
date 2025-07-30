package com.aitherapist.aitherapist.repositories;

import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface IDailyHealthDataRepository extends JpaRepository<DailyHealthData, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM DailyHealthData d WHERE d.patient.id = :patientId")
    void deleteAllByPatientId(Long patientId);
}
