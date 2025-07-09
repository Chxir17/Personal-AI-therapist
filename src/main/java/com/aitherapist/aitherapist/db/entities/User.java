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

    @Column(name = "chronic_diseases")
    private String chronicDiseases;

    @Column(name = "height")
    private Double height;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "bad_habits")
    private String badHabits;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<HealthData> healthDataList = new ArrayList<>();
    @Override
    public String toString() {
        return "🙌 Спасибо за ответ!\n" +
                "🆔 ID пользователя: " + id + "\n" +
                "👤 Имя: " + name + "\n" +
                "🎂 Возраст: " + (age != null ? age + " лет" : "не указано") + "\n" +
                "⚧ Пол: " + (gender == null ? "не указан" : (gender ? "мужской" : "женский")) + "\n" +
                "🩺 Хронические заболевания: " + (chronicDiseases != null ? chronicDiseases : "не указаны") + "\n" +
                "📏 Рост: " + (height != null ? height + " см" : "не указан") + "\n" +
                "⚖️ Вес: " + (weight != null ? weight + " кг" : "не указан") + "\n" +
                "🚬 Плохие привычки: " + (badHabits != null ? badHabits : "не указаны") + "\n" +
                "💙 Берегите себя!";
    }

}