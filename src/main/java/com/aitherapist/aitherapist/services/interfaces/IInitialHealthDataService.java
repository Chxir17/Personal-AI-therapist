package com.aitherapist.aitherapist.services.interfaces;

import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;

public interface IInitialHealthDataService {
    void putInitialHealthDataByUserId(InitialHealthData initialHealthDataService, Long userId);
    InitialHealthData getInitialHealthDataByUserId(Long userId);
    void updateInitialHealthDataByUserId(InitialHealthData initialHealthData, Long userId);
}
