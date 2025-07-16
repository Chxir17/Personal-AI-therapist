package com.aitherapist.aitherapist.domain.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import jakarta.persistence.*;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "initial_health_data")
public class InitialHealthData implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "heart_pain")
    private Boolean heartPain;

    @Column(name = "arrhythmia")
    private Boolean arrhythmia;

    @Column(name = "chronic_diseases")
    private String chronicDiseases;

    @Column(name = "height")
    private Double height;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "bad_habits")
    private String badHabits;

    @OneToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;


    @Override
    public String toString() {
        return "InitialHealthData{" +
                "id=" + id +
                ", heartPain=" + heartPain +
                ", arrhythmia=" + arrhythmia +
                ", chronicDiseases='" + chronicDiseases + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", badHabits='" + badHabits + '\'' +
                '}';
    }
}
