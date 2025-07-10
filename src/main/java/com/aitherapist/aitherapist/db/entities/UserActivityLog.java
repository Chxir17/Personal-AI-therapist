package com.aitherapist.aitherapist.db.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * UserActivityLog - for pusher message. Keep time user activity.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_activity_logs")
public class UserActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "action_time", nullable = false)
    private LocalDateTime actionTime;

    @Column(name = "action_type", nullable = false)
    private String actionType;

    @Column(name = "message_id")
    private Long messageId;
}