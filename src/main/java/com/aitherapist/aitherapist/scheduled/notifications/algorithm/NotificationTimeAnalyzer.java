package com.aitherapist.aitherapist.scheduled.notifications.algorithm;

import com.aitherapist.aitherapist.db.entities.UserActivityLog;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class NotificationTimeAnalyzer {
    public Map<DayOfWeek, LocalTime> getBestNotificationTimesForUser(List<UserActivityLog> userLogs) {
        List<UserActivityLog> sortedLogs = userLogs.stream().sorted(Comparator.comparing(UserActivityLog::getActionTime)).toList();
        Map<DayOfWeek, List<LocalTime>> timeByDay = new HashMap<>();
        for (int i = 0; i < sortedLogs.size(); i++) {
            UserActivityLog log = sortedLogs.get(i);
            if ("read".equalsIgnoreCase(log.getActionType())) {
                LocalDateTime readTime = log.getActionTime();
                LocalDateTime replyTime = null;
                for (int j = i + 1; j < sortedLogs.size(); j++) {
                    UserActivityLog nextLog = sortedLogs.get(j);
                    if (!"write".equalsIgnoreCase(nextLog.getActionType())) {
                        continue;
                    }
                    long hoursBetween = Duration.between(readTime, nextLog.getActionTime()).toHours();
                    if (hoursBetween <= 2) {
                        replyTime = nextLog.getActionTime();
                    }
                    break;
                }
                if (replyTime != null) {
                    LocalDateTime notificationTime = readTime.minusMinutes(10);
                    DayOfWeek day = notificationTime.getDayOfWeek();
                    timeByDay.computeIfAbsent(day, d -> new ArrayList<>()).add(notificationTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES));
                }
            }
        }

        Map<DayOfWeek, LocalTime> result = new HashMap<>();
        for (Map.Entry<DayOfWeek, List<LocalTime>> entry : timeByDay.entrySet()) {
            result.put(entry.getKey(), mostCommonTime(entry.getValue()));
        }

        return result;
    }

    private LocalTime mostCommonTime(List<LocalTime> times) {
        if (times.isEmpty()) {
            return LocalTime.of(11, 0);
        }

        Map<LocalTime, Long> counts = times.stream()
                .collect(Collectors.groupingBy(
                        time -> time.truncatedTo(ChronoUnit.MINUTES),
                        Collectors.counting()
                ));

        long maxCount = counts.values().stream().max(Long::compare).orElse(1L);

        boolean allUnique = counts.values().stream().allMatch(count -> count == 1);

        if (allUnique) {
            return averageTime(times);
        } else {
            return counts.entrySet().stream()
                    .filter(entry -> entry.getValue() == maxCount)
                    .map(Map.Entry::getKey)
                    .sorted()
                    .findFirst()
                    .orElse(LocalTime.of(11, 0));
        }
    }

    private LocalTime averageTime(List<LocalTime> times) {
        int totalMinutes = times.stream().mapToInt(time -> time.getHour() * 60 + time.getMinute()).sum();
        int avgMinutes = totalMinutes / times.size();
        int avgHour = avgMinutes / 60;
        int avgMinute = avgMinutes % 60;
        return LocalTime.of(avgHour, avgMinute);
    }

}
