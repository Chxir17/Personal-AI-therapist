package com.aitherapist.aitherapist.services.interfaces;
import com.aitherapist.aitherapist.domain.model.entities.HealthData;
import java.util.List;

/**
 * IHealthDataService - Service interface for health data entity.
 * Defines methods for CRUD operations and additional business logic
 */
public interface IHealthDataService {
    HealthData saveHealthDataInUser(int userId, HealthData healthData);
    List<HealthData> fetchHealthDataList(Integer userId);
    HealthData updateHealthData(HealthData healthData, Integer userId);
    void deleteHealthData(Integer userId);
}
