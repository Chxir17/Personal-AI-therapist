package com.aitherapist.aitherapist.domain.model.entities;

import com.aitherapist.aitherapist.domain.enums.Roles;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "user_type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Doctor.class, name = "DOCTOR"),
        @JsonSubTypes.Type(value = ClinicPatient.class, name = "CLINIC_PATIENT"),
        @JsonSubTypes.Type(value = NonClinicPatient.class, name = "NON_CLINIC_PATIENT")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private Boolean gender;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserActivityLog> activityLogs = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private NotificationConfig notificationConfig;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Integer getAge() {
        if (birthDate == null) return null;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public Integer getAgeInMonths() {
        if (birthDate == null) return null;
        return Period.between(birthDate, LocalDate.now()).getMonths();
    }

    public Integer getAgeInDays() {
        if (birthDate == null) return null;
        return Period.between(birthDate, LocalDate.now()).getDays();
    }

    public String getFormattedAge() {
        if (birthDate == null) return "Не указана";

        Period period = Period.between(birthDate, LocalDate.now());
        return String.format("%d лет, %d месяцев, %d дней",
                period.getYears(), period.getMonths(), period.getDays());
    }

    public int getYears() {
        return (Period.between(birthDate, LocalDate.now()).getYears());
    }

    public int getMonths() {
        return (Period.between(birthDate, LocalDate.now()).getMonths());
    }

    public int getDays() {
        return (Period.between(birthDate, LocalDate.now()).getDays());
    }
}
