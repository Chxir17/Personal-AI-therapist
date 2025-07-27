package com.aitherapist.aitherapist.interactionWithGigaApi.utils;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.ModelName;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LLM {

    private final AccessToken accessToken;

    @Autowired
    public LLM(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public String talkToChat(List<ChatMessage> messages) {
        return talkToChat(messages, 1);
    }

    public String talkToChat(List<ChatMessage> messages, int model) {
        try {
            GigaChatClient client = GigaChatClient.builder().authClient(AuthClient.builder()
                            .withProvidedTokenAuth(accessToken.getToken()).build())
                    .build();

            CompletionResponse response = client.completions(CompletionRequest.builder()
                    .model(selectModel(model))
                    .messages(messages)
                    .build());

            return response.choices().get(0).message().content();
        } catch (HttpClientException ex) {
            return ex.statusCode() + " " + ex.bodyAsString();
        }
    }

    private String selectModel(int modelNum) {
        return switch (modelNum) {
            case 2 -> ModelName.GIGA_CHAT_PRO;
            case 3 -> ModelName.GIGA_CHAT_MAX;
            default -> ModelName.GIGA_CHAT;
        };
    }
}
