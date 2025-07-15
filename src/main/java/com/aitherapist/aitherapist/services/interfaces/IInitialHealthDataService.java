package com.aitherapist.aitherapist.services.interfaces;

import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;

public interface IInitialHealthDataService {
    void putInitialHealthDataByUserId(IInitialHealthDataService initialHealthDataService, Long userId);
    InitialHealthData getInitialHealthDataByUserId(IInitialHealthDataService initialHealthDataService, Long userId);

}
