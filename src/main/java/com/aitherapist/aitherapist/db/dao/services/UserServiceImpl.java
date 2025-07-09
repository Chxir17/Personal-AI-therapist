package com.aitherapist.aitherapist.db.dao.services;
import com.aitherapist.aitherapist.db.dao.repositorys.IUserRepository;
import com.aitherapist.aitherapist.db.entities.User;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserServiceImpl - implements IUserService (provides main operation)
 * IUserRepository - repository-interface. auto create interface with implemented main methods.
 * @Transactional annotation helps in one transaction do work.
 */
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Override
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User fetchUser(Integer id) {
        return userRepository.getOne(id);
    }

    /**
     * findById, copyProperties - function from spring.
     * replace multi set.properties.
     * @param user
     * @param id
     * @return save in db and return User
     */
    @Override
    @Transactional
    public User updateUser(User user, Integer id) {
        User currentUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BeanUtils.copyProperties(user, currentUser, "id"); // ignore id, чтобы
        return userRepository.save(currentUser);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void createUser(int userId, User user) {
        user.setId(userId);
        userRepository.save(user);
    }

    @Override
    public void deleteUserIfExists(Integer id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.delete(userRepository.findById(id).get());
        }
    }

    @Override
    public User getUser(Integer id) {
        User user =  userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return user;
    }
}
