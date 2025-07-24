package com.aitherapist.aitherapist.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserRegistrationDto {
    private String user_type;
    private String name;
    private LocalDate birthDate;
    private Boolean gender;
}
