package com.aitherapist.aitherapist.functionality.QAChatBot.QAChatBot;

import chat.giga.model.completion.ChatMessage;
import com.aitherapist.aitherapist.domain.enums.Prompts;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.interactionWithGigaApi.utils.LLM;
import org.aspectj.util.IStructureModel;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserQuestions {
    public static String answerUserQuestion(Patient patient, String userMessage, List<Message> history){
        Map<String, String> metaInfo = patient.makeMetaInformation(patient);
        Map<String, String> parametersHistory = patient.buildMedicalHistory();
        Prompts prompt = Prompts.valueOf("CHAT_BOT_PROMPT");
        String systemPrompt = prompt.getMessage();
        List<ChatMessage> requestMessages = new ArrayList<>();
        requestMessages.add(ChatMessage.builder()
                .role(ChatMessage.Role.SYSTEM)
                .content(systemPrompt)
                .build());
        if (history != null && !history.isEmpty()) {
            for (Message msg : history) {
                if (msg.getText() != null && !msg.getText().isEmpty()) {
                    requestMessages.add(ChatMessage.builder()
                            .role(ChatMessage.Role.USER)
                            .content(msg.getText())
                            .build());
                }
            }
        }

        String fullMessage = userMessage + metaInfo + parametersHistory;
        requestMessages.add(ChatMessage.builder()
                .role(ChatMessage.Role.USER)
                .content(fullMessage)
                .build());
        String response = LLM.talkToChat(requestMessages);
        if (Objects.equals(response, "null")){

        }
        return response;

    }
}
