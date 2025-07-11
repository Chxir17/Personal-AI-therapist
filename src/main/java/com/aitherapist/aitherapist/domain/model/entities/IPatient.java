package com.aitherapist.aitherapist.domain.model.entities;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
public interface IPatient {
    List<HealthData> getHealthData();

}
