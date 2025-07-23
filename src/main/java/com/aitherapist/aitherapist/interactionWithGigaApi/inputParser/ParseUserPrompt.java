package com.aitherapist.aitherapist.interactionWithGigaApi.inputParser;

import chat.giga.model.completion.ChatMessage;
import com.aitherapist.aitherapist.domain.enums.Prompts;
import com.aitherapist.aitherapist.interactionWithGigaApi.utils.LLM;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ParseUserPrompt {
    public static String patientRegistrationParser(String userMessage) throws InterruptedException {
        Prompts prompt = Prompts.valueOf("PATIENT_REGISTRATION_PROMPT");
        String systemPrompt = prompt.getMessage();
        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );
        //FIXME вынести это
        String response = "";
        for (int i = 0; i < 10; i++) {
            try {
                response = LLM.talkToChat(requestMessage);
                break;
            } catch (Exception e) {
                if (i == 10 - 1) throw e;
                Thread.sleep(1000);
            }
        }
        return response;
    }


    public static String doctorRegistrationParser(String userMessage) throws InterruptedException {
        Prompts prompt = Prompts.valueOf("DOCTOR_REGISTRATION_PROMPT");
        String systemPrompt = prompt.getMessage();
        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );
        //FIXME вынести это
        String response = "";
        for (int i = 0; i < 10; i++) {
            try {
                response = LLM.talkToChat(requestMessage);
                break;
            } catch (Exception e) {
                if (i == 10 - 1) throw e;
                Thread.sleep(1000);
            }
        }
        return response;
    }


    public static String dailyQuestionnaireParser(String userMessage){
        Prompts prompt = Prompts.valueOf("DAILY_QUESTIONNAIRE_PROMPT");
        String systemPrompt = prompt.getMessage();
        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );
        return LLM.talkToChat(requestMessage);
    }

    public static String parameterEditorParser(String userMessage){
        Prompts prompt = Prompts.valueOf("PARAMETERS_EDITOR_PROMPT");
        String systemPrompt = prompt.getMessage();
        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );
        return LLM.talkToChat(requestMessage);
    }
}
