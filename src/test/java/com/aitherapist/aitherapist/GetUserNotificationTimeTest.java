package com.aitherapist.aitherapist;

import com.aitherapist.aitherapist.domain.model.entities.UserActivityLog;
import com.aitherapist.aitherapist.scheduled.notifications.algorithm.GetUserNotificationTime;
import com.aitherapist.aitherapist.services.activity.UserActivityRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetUserNotificationTimeTest {

    private GetUserNotificationTime getUserNotificationTime;
    private UserActivityRegistrationService timeLogsMock;

    @BeforeEach
    void setUp() {
        timeLogsMock = mock(UserActivityRegistrationService.class);
        getUserNotificationTime = new GetUserNotificationTime();

        // Подставим моком сервис в поле (если нет конструктора или сеттера)
        injectField(getUserNotificationTime, "timeLogs", timeLogsMock);
    }

    @Test
    void testGetTimeForNotificationById() {
        long userId = 1L;

        // Делаем фиктивные логи пользователя
        List<UserActivityLog> logs = new ArrayList<>();
        LocalDateTime readTime = LocalDateTime.of(2025, 7, 10, 12, 30);
        LocalDateTime writeTime = readTime.plusMinutes(30);

        logs.add(buildLog("read", readTime));
        logs.add(buildLog("write", writeTime));

        when(timeLogsMock.getByUserId(userId)).thenReturn(logs);

        Map<DayOfWeek, LocalTime> result = getUserNotificationTime.getTimeForNotificationById(userId);

        // Ожидаем, что уведомление будет на 10 минут раньше readTime
        LocalTime expectedTime = readTime.minusMinutes(10).toLocalTime().truncatedTo(ChronoUnit.MINUTES);
        DayOfWeek day = readTime.minusMinutes(10).getDayOfWeek();

        assertEquals(expectedTime, result.get(day));
    }

    private UserActivityLog buildLog(String type, LocalDateTime time) {
        UserActivityLog log = new UserActivityLog();
        log.setActionType(type);
        log.setActionTime(time);
        return log;
    }

    // Для установки приватного поля timeLogs без сеттера или конструктора
    private void injectField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
