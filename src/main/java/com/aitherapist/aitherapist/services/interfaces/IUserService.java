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
    User fetchUser(Integer id);
    User updateUser(User user, Integer id);
    void deleteUser(Integer id);
    void createUser(int userId, User user);
    void deleteUserIfExists(Integer id);
    User getUser(Integer id);
    Boolean checkIfUserExists(Integer userId);
    Roles getUserRoles(Integer userId);
    Boolean isUserInClinic(Integer userId);
    void changeUserRoles(Integer userId, Roles role);
    void editUserInformation(User user, int id);
    void editUserHealthData(User user, int id, HealthData healthData);
}
