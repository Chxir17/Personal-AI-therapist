package com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorRegistrationState {
    private int currentStep = 1;
    private final StringBuilder userInput = new StringBuilder();
}
