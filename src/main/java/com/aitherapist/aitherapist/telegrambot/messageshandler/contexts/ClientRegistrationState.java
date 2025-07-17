package com.aitherapist.aitherapist.telegrambot.messageshandler.contexts;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ClientRegistrationState {
    private int currentStep = 1;
    private final StringBuilder additional = new StringBuilder();
    private final StringBuilder base = new StringBuilder();
}
