package com.aitherapist.aitherapist.services.interfaces;
import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;

import java.util.List;

/**
 * IHealthDataService - Service interface for health data entity.
 * Defines methods for CRUD operations and additional business logic
 */
public interface IHealthDataService {
    DailyHealthData saveHealthDataInUser(Long userId, DailyHealthData dailyHealthData);
    List<DailyHealthData> fetchHealthDataList(Long userId);
    DailyHealthData updateHealthData(DailyHealthData dailyHealthData, Long userId);
    void deleteHealthData(Long userId);
}
