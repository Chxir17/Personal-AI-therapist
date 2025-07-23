package com.aitherapist.aitherapist.interactionWithGigaApi.utils;

import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.*;
import chat.giga.client.*;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;

import java.util.List;

public final class LLM {
    public static String talkToChat(List<ChatMessage> messages) {
        GigaChatClient client = GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withProvidedTokenAuth(AccessToken.getInstance().getToken()).build())
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

    private static String selectModel(int modelNum){
        return switch (modelNum) {
            case 2 -> ModelName.GIGA_CHAT_PRO;
            case 3 -> ModelName.GIGA_CHAT_MAX;
            default -> ModelName.GIGA_CHAT;
        };
    }

    public static String talkToChat(List<ChatMessage> messages, int model) {

        GigaChatClient client = GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withProvidedTokenAuth(AccessToken.getInstance().getToken()).build())
                .build();
        try {
            CompletionResponse response = client.completions(CompletionRequest.builder()
                    .model(selectModel(model))
                    .messages(messages)
                    .build());
            return response.choices().get(0).message().content();
        } catch (HttpClientException ex) {
            return ex.statusCode() + " " + ex.bodyAsString();
        }
    }
}





