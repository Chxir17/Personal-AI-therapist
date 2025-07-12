package com.aitherapist.aitherapist.services.interfaces;
import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.model.entities.HealthData;
import com.aitherapist.aitherapist.domain.model.entities.User;

/**
 * IUserService - Service interface for health data entity.
 * Defines methods for CRUD operations and additional business logic
 */
public interface IUserService {
    User saveUser(User user);
    User fetchUser(Long id);
    User updateUser(User user, Long id);
    void deleteUser(Long id);
    void createUser(Long userId, User user);
    void deleteUserIfExists(Long id);
    User getUser(Long id);
    Boolean checkIfUserExists(Long userId);
    Roles getUserRoles(Long userId);
    Boolean isUserInClinic(Long userId);
    void changeUserRoles(Long userId, Roles role);
    void editUserInformation(User user, Long id);
    void editUserHealthData(User user, Long id, HealthData healthData);
}
