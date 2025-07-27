package com.aitherapist.aitherapist.domain.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_configs")
public class NotificationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "notifications_enabled", nullable = false)
    private boolean notificationsEnabled = true;

    @Column(name = "notification_time")
    private LocalTime notificationUserTime;

    @Column(name = "custom_message", columnDefinition = "TEXT")
    private String customMessage;

    public Boolean isNotificationEnabled() {
        return this.notificationsEnabled;
    }
}