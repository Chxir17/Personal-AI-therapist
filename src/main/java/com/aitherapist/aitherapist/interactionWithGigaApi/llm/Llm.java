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


    public static String talkToChat(String accessToken, List<ChatMessage> messages, int model) {
        String modelName;
        switch (model) {
            case 1:
                modelName = ModelName.GIGA_CHAT;
                break;
            case 2:
                modelName = ModelName.GIGA_CHAT_PRO;
                break;
            case 3:
                modelName = ModelName.GIGA_CHAT_MAX;
                break;
            default:
                return "Ошибка: неизвестный номер модели. Допустимые значения: 1, 2 или 3.";
        }
        GigaChatClient client = GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withProvidedTokenAuth(accessToken).build())
                .build();
        try {
            CompletionResponse response = client.completions(CompletionRequest.builder()
                    .model(modelName)
                    .messages(messages)
                    .build());
            return response.choices().get(0).message().content();
        } catch (HttpClientException ex) {
            return ex.statusCode() + " " + ex.bodyAsString();
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

}





