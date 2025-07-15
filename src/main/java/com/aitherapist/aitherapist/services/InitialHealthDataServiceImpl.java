package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;
import com.aitherapist.aitherapist.repositories.IInitialHealthDataRepository;
import com.aitherapist.aitherapist.services.interfaces.IInitialHealthDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InitialHealthDataServiceImpl implements IInitialHealthDataService {
    @Autowired
    IInitialHealthDataRepository iInitialHealthDataRepository;

    @Override
    @Transactional
    public void putInitialHealthDataByUserId(IInitialHealthDataService initialHealthDataService, Long userId) {
        initialHealthDataService.putInitialHealthDataByUserId(initialHealthDataService, userId);
    }

    @Override
    @Transactional
    
    public InitialHealthData getInitialHealthDataByUserId(IInitialHealthDataService initialHealthDataService, Long userId) {
        return initialHealthDataService.getInitialHealthDataByUserId(initialHealthDataService, userId);
    }
}
