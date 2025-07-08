package com.aitherapist.aitherapist;

import com.aitherapist.aitherapist.db.dao.services.UserServiceImpl;
import com.aitherapist.aitherapist.db.entities.HealthData;
import com.aitherapist.aitherapist.db.entities.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") // Используем тестовый профиль
@Transactional
public class DatabaseIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    private static final String TEST_USER_NAME = "Test User";
    private Integer testUserId;

    @BeforeEach
    public void setup() {
        userService.fetchUserList(null).stream()
                .filter(u -> TEST_USER_NAME.equals(u.getName()))
                .forEach(u -> userService.deleteUser(u.getId()));

        User user = User.builder()
                .name(TEST_USER_NAME)
                .age(30)  // пример обязательного поля
                .male(true)  // пример обязательного поля
                .build();

        User savedUser = userService.saveUser(user);
        testUserId = savedUser.getId();
        assertNotNull(testUserId, "ID пользователя не должен быть null");
    }

    @AfterEach
    public void cleanup() {
        if (testUserId != null) {
            userService.deleteUserIfExists(testUserId);
        }
    }

    @Test
    public void testUserCreation() {
        User foundUser = userService.fetchUserList(null).stream()
                .filter(u -> u.getId().equals(testUserId))
                .findFirst()
                .orElse(null);

        assertNotNull(foundUser, "Пользователь должен существовать в базе");
        assertEquals(TEST_USER_NAME, foundUser.getName(), "Имя пользователя должно совпадать");
    }

    @Test
    public void testHealthDataSaving() {
        HealthData healthData = HealthData.builder()
                .pulse(72)
                .temperature(36.6)
                .bloodOxygenLevel(98.5)
                .build();

        User user = userService.fetchUserList(null).stream()
                .filter(u -> u.getId().equals(testUserId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Пользователь не найден"));

        user.getHealthDataList().add(healthData);
        healthData.setUser(user);

        userService.saveUser(user);

        User updatedUser = userService.fetchUserList(null).stream()
                .filter(u -> u.getId().equals(testUserId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Пользователь не найден после сохранения"));

        assertFalse(updatedUser.getHealthDataList().isEmpty(),
                "Список health data не должен быть пустым");
        assertEquals(72, updatedUser.getHealthDataList().get(0).getPulse(),
                "Пульс должен соответствовать сохраненному значению");
    }

    @Test
    public void testUserDeletion() {
        userService.deleteUser(testUserId);

        boolean userExists = userService.fetchUserList(null).stream()
                .anyMatch(u -> u.getId().equals(testUserId));

        assertFalse(userExists, "Пользователь должен быть удален из базы");
    }
}