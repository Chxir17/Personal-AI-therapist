package com.aitherapist.aitherapist.interactionWithGigaApi.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class AccessToken {
    private static final Duration TOKEN_TTL = Duration.ofMinutes(29);
    private String token;
    private Instant lastUpdated = Instant.EPOCH;

    private final TokenFetcher tokenFetcher;

    @Autowired
    public AccessToken(TokenFetcher tokenFetcher) {
        this.tokenFetcher = tokenFetcher;
    }

    public synchronized String getToken() {
        if (token == null || needsRefresh()) {
            refreshToken();
        }
        return token;
    }

    private boolean needsRefresh() {
        return Duration.between(lastUpdated, Instant.now()).compareTo(TOKEN_TTL) > 0;
    }

    private void refreshToken() {
        token = tokenFetcher.fetchNewToken();
        lastUpdated = Instant.now();
    }
}




