package com.aitherapist.aitherapist.services.interfaces;
import com.aitherapist.aitherapist.domain.enums.Roles;
import com.aitherapist.aitherapist.domain.model.entities.*;

/**
 * IUserService - Service interface for health data entity.
 * Defines methods for CRUD operations and additional business logic
 */
public interface IUserService {
    void saveUser(User user);
    User fetchUserByTelegramId(Long id);
    void updateUser(User user, Long id);
    User getUser(Long id);
    Boolean checkIfUserExists(Long userId);
    Roles getUserRoles(Long userId);
    void editUserInformation(User user, Long id);
    void editUserHealthData(User user, Long id, DailyHealthData dailyHealthData);
    Boolean isSignUp(Long userId);
    User getUserByUserId(Long userId);
    ClinicPatient getClinicPatientById(Long clinicPatientId);
}
