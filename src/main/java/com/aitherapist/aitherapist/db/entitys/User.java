package com.aitherapist.aitherapist.db.entitys;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * User - class with main information.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class User {
    public int id;
    public String name;
    public int age;
    public String male;
    public String chronicDiseases;
    public int height;
    public int weight;
    public String badHabits;

}
