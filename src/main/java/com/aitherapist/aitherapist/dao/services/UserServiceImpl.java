package com.aitherapist.aitherapist.dao.services;
import com.aitherapist.aitherapist.dao.repositorys.IHealthDataRepository;
import com.aitherapist.aitherapist.dao.repositorys.IUserRepository;
import com.aitherapist.aitherapist.db.entities.User;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * UserServiceImpl - implements IUserService (provides main operation)
 * IUserRepository - repository-interface. auto create interface with implemented main methods.
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> fetchUserList(Integer id) {
        return userRepository.findAll();
    }

    /**
     * findById, copyProperties - function from spring.
     * replace multi set.properties.
     * @param user
     * @param id
     * @return save in db and return User
     */
    @Override
    public User updateUser(User user, Integer id) {
        User currentUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BeanUtils.copyProperties(user, currentUser, "id"); // ignore id, чтобы
        return userRepository.save(currentUser);
    }

    @Override
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }
}
