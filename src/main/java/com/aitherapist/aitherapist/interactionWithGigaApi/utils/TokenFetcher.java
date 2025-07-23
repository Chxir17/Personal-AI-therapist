package com.aitherapist.aitherapist.interactionWithGigaApi.utils;

import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.model.Scope;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class TokenFetcher {

    @Retryable(
            value = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public String fetchNewToken() {
        String authKey = System.getenv("GIGA_CHAT_API_KEY");

        AuthClient client = AuthClient.builder().withOAuth(AuthClientBuilder.OAuthBuilder.builder().scope(Scope.GIGACHAT_API_PERS).authKey(authKey).build()).build();
        String token = client.getToken().token();
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Failed to fetch non-empty token");
        }
        return token;
    }

    @Retryable(
            value = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public String fetchNewToken(Scope scope) {
        String authKey = System.getenv("GIGA_CHAT_API_KEY");
        AuthClient client = AuthClient.builder().withOAuth(AuthClientBuilder.OAuthBuilder.builder().scope(scope).authKey(authKey).build()).build();
        String token = client.getToken().token();
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Failed to fetch non-empty token with scope");
        }
        return token;
    }
}
