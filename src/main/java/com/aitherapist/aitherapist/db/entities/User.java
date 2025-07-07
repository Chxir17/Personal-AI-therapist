package com.aitherapist.aitherapist.db.entities;

import lombok.*;

import jakarta.persistence.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @NonNull
    private Integer id;

    @NonNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "male")
    private Boolean male;

    @Column(name = "chronic_diseases")
    private String chronicDiseases;

    @Column(name = "height")
    private Double height;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "bad_habits")
    private String badHabits;

    //@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<HealthData> healthDataList = new ArrayList<>();
    @Override
    public String toString() {
        return "🙌 Спасибо за ответ!\n" +
                "🆔 ID пользователя: " + id + "\n" +
                "👤 Имя: " + name + "\n" +
                "🎂 Возраст: " + (age != null ? age + " лет" : "не указано") + "\n" +
                "⚧ Пол: " + (male == null ? "не указан" : (male ? "мужской" : "женский")) + "\n" +
                "🩺 Хронические заболевания: " + (chronicDiseases != null ? chronicDiseases : "не указаны") + "\n" +
                "📏 Рост: " + (height != null ? height + " см" : "не указан") + "\n" +
                "⚖️ Вес: " + (weight != null ? weight + " кг" : "не указан") + "\n" +
                "🚬 Плохие привычки: " + (badHabits != null ? badHabits : "не указаны") + "\n" +
                "💙 Берегите себя!";
    }

}