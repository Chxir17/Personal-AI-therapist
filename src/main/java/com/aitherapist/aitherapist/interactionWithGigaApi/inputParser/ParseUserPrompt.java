package com.aitherapist.aitherapist.interactionWithGigaApi.inputParser;

import chat.giga.model.completion.ChatMessage;
import com.aitherapist.aitherapist.domain.enums.Prompts;
import com.aitherapist.aitherapist.interactionWithGigaApi.utils.LLM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Arrays;
import java.util.List;




@Component
public class ParseUserPrompt {

    private final LLM llm;

    @Autowired
    public ParseUserPrompt(LLM llm) {
        this.llm = llm;
    }

    @Retryable(
            value = { Exception.class },
            maxAttempts = 10,
            backoff = @Backoff(delay = 1000)
    )
    public String patientRegistrationParser(String userMessage) {
        Prompts prompt = Prompts.valueOf("PATIENT_REGISTRATION_PROMPT");
        String systemPrompt = prompt.getMessage();
        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );
        return llm.talkToChat(requestMessage);
    }

    @Retryable(
            value = { Exception.class },
            maxAttempts = 10,
            backoff = @Backoff(delay = 1000)
    )
    public String doctorRegistrationParser(String userMessage) {
        Prompts prompt = Prompts.valueOf("DOCTOR_REGISTRATION_PROMPT");
        String systemPrompt = prompt.getMessage();
        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );
        return llm.talkToChat(requestMessage);
    }

    public String dailyQuestionnaireParser(String userMessage) {
        Prompts prompt = Prompts.valueOf("DAILY_QUESTIONNAIRE_PROMPT");
        String systemPrompt = prompt.getMessage();
        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );
        return llm.talkToChat(requestMessage);
    }

    public String parameterEditorParser(String userMessage) {
        Prompts prompt = Prompts.valueOf("PARAMETERS_EDITOR_PROMPT");
        String systemPrompt = prompt.getMessage();
        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );

        String ans =  llm.talkToChat(requestMessage);
        return ans;
    }
}

