package com.aitherapist.aitherapist.services.interfaces;
import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.domain.model.entities.UserActivityLog;

/**
 * IUserService - Service interface for health data entity.
 * Defines methods for CRUD operations and additional business logic
 */
public interface IUserService {
    void saveUser(User user);
    User fetchUserByTelegramId(Long id);
    void updateUser(User user, Long id);
    void deleteUser(Long id);
    void deleteUserIfExists(Long id);
    User getUser(Long id);
    Boolean checkIfUserExists(Long userId);
    Roles getUserRoles(Long userId);
    Boolean isUserInClinic(Long userId);
    void changeUserRoles(Long userId, Roles role);
    void editUserInformation(User user, Long id);
    void editUserHealthData(User user, Long id, DailyHealthData dailyHealthData);
    void addActivityLog(User user, String actionType, Long messageId);
    UserActivityLog getUserActivityLog(User user, Long id);
    void editActivityLog(User user, Long id, UserActivityLog userActivityLog);
    Boolean isSignUp(Long userId);
    User getUserByUserId(Long userId);
    void registerUser(Long userId, User user);
}
