package com.aitherapist.aitherapist.services.interfaces;
import com.aitherapist.aitherapist.domain.model.entities.dailyHealthData;

import java.util.List;

/**
 * IHealthDataService - Service interface for health data entity.
 * Defines methods for CRUD operations and additional business logic
 */
public interface IHealthDataService {
    dailyHealthData saveHealthDataInUser(Long userId, dailyHealthData dailyHealthData);
    List<dailyHealthData> fetchHealthDataList(Long userId);
    dailyHealthData updateHealthData(dailyHealthData dailyHealthData, Long userId);
    void deleteHealthData(Long userId);
}
