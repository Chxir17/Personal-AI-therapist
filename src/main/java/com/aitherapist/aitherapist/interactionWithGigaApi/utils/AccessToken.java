package com.aitherapist.aitherapist.interactionWithGigaApi.utils;

import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.model.Scope;

import java.time.Duration;
import java.time.Instant;

public class AccessToken {
    private static final Duration TOKEN_TTL = Duration.ofMinutes(29);
    private String token;
    private Instant lastUpdated = Instant.EPOCH;

    private AccessToken() {}

    private static class Holder {
        private static final AccessToken INSTANCE = new AccessToken();
    }

    public static AccessToken getInstance() {
        return Holder.INSTANCE;
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
        token = fetchNewToken();
        lastUpdated = Instant.now();
    }

    private String fetchNewToken() {
        try {
            String authKey = System.getenv("GIGA_CHAT_API_KEY");
            AuthClient client = AuthClient.builder().withOAuth(AuthClientBuilder.OAuthBuilder.builder()
                    .scope(Scope.GIGACHAT_API_PERS)
                    .authKey(authKey)
                    .build()).build();
            return client.getToken().token();
        } catch (Exception e) {
            return null;
        }
    }

    private String fetchNewToken(Scope scope) {
        try {
            String authKey = System.getenv("GIGA_CHAT_API_KEY");
            AuthClient client = AuthClient.builder().withOAuth(AuthClientBuilder.OAuthBuilder.builder()
                    .scope(scope)
                    .authKey(authKey)
                    .build()).build();
            return client.getToken().token();
        } catch (Exception e) {
            return null;
        }
    }
}
