package com.aitherapist.aitherapist.db.entities;

import lombok.*;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User - custom Object for Hibernate
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    @NonNull
    private Integer id;

    @NonNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private Boolean gender;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserActivityLog> activityLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<HealthData> healthDataList = new ArrayList<>();

    private Roles roles;

}