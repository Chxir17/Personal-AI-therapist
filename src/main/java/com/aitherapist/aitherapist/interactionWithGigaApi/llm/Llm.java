package com.aitherapist.aitherapist.interactionWithGigaApi.llm;

import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.*;
import chat.giga.client.*;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;

import java.util.List;


public final class Llm {
    /**
     * Получение токена из SDK GigaChat.
     * @param scope   область действия
     * @return access_token или null
     */
    public static String getGigaChatToken(Scope scope) {
        try {
            String authKey = System.getenv("GIGA_CHAT_API_KEY");
            AuthClient client = AuthClient.builder().withOAuth(AuthClientBuilder.OAuthBuilder.builder()
                                    .scope(scope)
                                    .authKey(authKey)
                                    .build()).build();
            return client.getToken().token();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getGigaChatToken() {
        try {
            String authKey = System.getenv("GIGA_CHAT_API_KEY");
            AuthClient client = AuthClient.builder().withOAuth(AuthClientBuilder.OAuthBuilder.builder()
                    .scope(Scope.GIGACHAT_API_PERS)
                    .authKey(authKey)
                    .build()).build();
            return client.getToken().token();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Отправка сообщения в чат GigaChat и получение ответа.
     * @param accessToken access_token
     * @param userMessage сообщение пользователя
     * @return ответ модели или null
     */

    public static String talkToChat(String accessToken, String userMessage, String model) {
        GigaChatClient client = GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withProvidedTokenAuth(accessToken).build())
                .build();
        try {
            CompletionResponse response = client.completions(CompletionRequest.builder()
                    .model(model)
                    .message(ChatMessage.builder()
                            .content(userMessage)
                            .role(ChatMessage.Role.USER)
                            .build())
                    .build());
            return response.choices().get(0).message().content();
        } catch (HttpClientException ex) {
            return ex.statusCode() + " " + ex.bodyAsString();
        }
    }

    public static String talkToChat(String accessToken, List<ChatMessage> messages) {
        GigaChatClient client = GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withProvidedTokenAuth(accessToken).build())
                .build();
        try {
            CompletionResponse response = client.completions(CompletionRequest.builder()
                    .model(ModelName.GIGA_CHAT)
                    .messages(messages)
                    .build());
            return response.choices().get(0).message().content();
        } catch (HttpClientException ex) {
            return ex.statusCode() + "  " + ex.bodyAsString();
        }
    }
}





