package com.aitherapist.aitherapist.services.interfaces;

import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;

public interface IInitialHealthDataService {
    InitialHealthData getInitialHealthDataByUserId(Long userId);
    void updateInitialHealthDataByUserId(Long userId,  InitialHealthData initialHealthData);
}
