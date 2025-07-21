package com.aitherapist.aitherapist.repositories;

import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDailyHealthDataRepository extends JpaRepository<DailyHealthData, Integer> {
}
