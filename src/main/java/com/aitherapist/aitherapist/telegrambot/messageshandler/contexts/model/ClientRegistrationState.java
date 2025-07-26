package com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRegistrationState {
    private int currentStep = 1;
    private final StringBuilder additional = new StringBuilder();
    private final StringBuilder base = new StringBuilder();
}
