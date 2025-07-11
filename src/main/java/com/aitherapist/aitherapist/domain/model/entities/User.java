package com.aitherapist.aitherapist.domain.model.entities;

import com.aitherapist.aitherapist.domain.enums.Roles;
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
@Entity
@Table(name = "users")
public abstract class User {
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

}