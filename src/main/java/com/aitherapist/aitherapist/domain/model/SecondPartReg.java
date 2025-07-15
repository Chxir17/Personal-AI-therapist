package com.aitherapist.aitherapist.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class SecondPartReg {
    public String arrhythmia;
    public String chronicDiseases;
    public String height;
    public String weight;
    public String badHabits;
    public int currentParam = 1;

    @Override
    public String toString() {
        return "SecondPartReg {\n" +
                "  arrhythmia = '" + arrhythmia + "',\n" +
                "  chronicDiseases = '" + chronicDiseases + "',\n" +
                "  height = '" + height + "',\n" +
                "  weight = '" + weight + "',\n" +
                "  badHabits = '" + badHabits + "'\n" +
                '}';
    }
}