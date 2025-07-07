package com.aitherapist.aitherapist.llm;

import chat.giga.client.auth.AccessToken;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.*;
import chat.giga.client.*;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.CompletionRequest;


public class Llm {

    /**
     * Получение токена из SDK GigaChat.
     *
     * @param authKey OAuth ключ
     * @param scope   область действия
     * @return access_token или null
     */
    public static AccessToken getGigaChatToken(String authKey, Scope scope) {
        try {
            AuthClient client = AuthClient.builder().withOAuth(AuthClientBuilder.OAuthBuilder.builder()
                                    .scope(scope)
                                    .authKey(authKey)
                                    .build()).build();
            return client.getToken();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Отправка сообщения в чат GigaChat и получение ответа.
     * @param accessToken access_token
     * @param userMessage сообщение пользователя
     * @param model модель (по умолчанию GigaChat:latest)
     * @return ответ модели или null
     */
    public static String talkToChat(String accessToken, String userMessage, String model, Scope scope) {
        GigaChatClient client = GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withOAuth(AuthClientBuilder.OAuthBuilder.builder()
                                .scope(scope)
                                .authKey(accessToken)
                                .build())
                        .build())
                .build();
        try {
            System.out.println(client.completions(CompletionRequest.builder()
                    .model(model)
                    .message(ChatMessage.builder()
                            .content(userMessage)
                            .role(ChatMessage.Role.USER)
                            .build())
                    .build()));
        } catch (HttpClientException ex) {
            System.out.println(ex.statusCode() + " " + ex.bodyAsString());
        }
        return accessToken;
    }

}





