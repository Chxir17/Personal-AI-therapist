package com.aitherapist.aitherapist.dao.services;
import com.aitherapist.aitherapist.db.entities.User;
import java.util.List;

/**
 * IUserService - Service interface for health data entity.
 * Defines methods for CRUD operations and additional business logic
 */
public interface IUserService {
    User saveUser(User user);
    List<User> fetchUserList(Integer id);
    User updateUser(User user, Integer id);
    void deleteUser(Integer id);
}
