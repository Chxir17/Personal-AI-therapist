package com.aitherapist.aitherapist.domain.model;


import jakarta.persistence.Entity;
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
public class FirstPartReg {
    public String name;
    public String age;
    public String gender;
    public int currentParam = 1;

    @Override
    public String toString() {
        return "FirstPartReg {\n" +
                "  name = '" + name + "',\n" +
                "  age = '" + age + "',\n" +
                "  gender = '" + gender + "',\n" +
                '}';
    }
}