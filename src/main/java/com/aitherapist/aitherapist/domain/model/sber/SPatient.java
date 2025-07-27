package com.aitherapist.aitherapist.domain.model.sber;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sber_patients")
public class SPatient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "snils", nullable = false, unique = true)
    private String snils;

    @Column(name = "comment_goal", columnDefinition = "TEXT")
    private String commentGoal;

}