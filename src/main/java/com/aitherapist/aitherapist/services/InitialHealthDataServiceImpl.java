package com.aitherapist.aitherapist.services;

import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;
import com.aitherapist.aitherapist.repositories.IInitialHealthDataRepository;
import com.aitherapist.aitherapist.services.interfaces.IInitialHealthDataService;
import org.springframework.beans.BeanUtils;
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
    public void putInitialHealthDataByUserId(InitialHealthData initialHealthData, Long userId) {
        initialHealthData.setId(userId);
        iInitialHealthDataRepository.save(initialHealthData);
    }

    @Override
    @Transactional
    
    public InitialHealthData getInitialHealthDataByUserId(Long userId) {
        return iInitialHealthDataRepository.getById(userId);
    }

    @Override
    public void updateInitialHealthDataByUserId(InitialHealthData initialHealthData, Long userId) {
        InitialHealthData i = getInitialHealthDataByUserId(userId);
        BeanUtils.copyProperties(initialHealthData, i);
    }
}
