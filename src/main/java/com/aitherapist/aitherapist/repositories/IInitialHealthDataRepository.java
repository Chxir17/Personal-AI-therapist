package com.aitherapist.aitherapist.repositories;

import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;
import com.aitherapist.aitherapist.services.interfaces.IInitialHealthDataService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IInitialHealthDataRepository extends JpaRepository<InitialHealthData, Long> {

}
