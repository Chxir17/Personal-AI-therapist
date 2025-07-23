package com.aitherapist.aitherapist.functionality.QAChatBot;

import chat.giga.model.completion.ChatMessage;
import com.aitherapist.aitherapist.domain.enums.ChatBotAnswers;
import com.aitherapist.aitherapist.domain.enums.Prompts;
import com.aitherapist.aitherapist.domain.model.entities.History;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.interactionWithGigaApi.utils.LLM;

import java.util.*;

public class UserQuestions {
    public static String answerUserQuestion(Patient patient, String userMessage, List<History> historyList) {
        Map<String, String> metaInfo = patient.makeMetaInformation(patient);
        Map<String, String> parametersHistory = patient.buildMedicalHistory();
        Prompts prompt = Prompts.valueOf("CHAT_BOT_PROMPT");
        String systemPrompt = prompt.getMessage();

        List<ChatMessage> requestMessages = new ArrayList<>();
        requestMessages.add(ChatMessage.builder().role(ChatMessage.Role.SYSTEM).content(systemPrompt).build());

        History userHistory = historyList.stream().filter(h -> Boolean.TRUE.equals(h.getRole())).findFirst().orElse(null);
        History botHistory = historyList.stream().filter(h -> Boolean.FALSE.equals(h.getRole())).findFirst().orElse(null);

        Queue<String> userMessages = userHistory != null ? new LinkedList<>(userHistory.getData()) : new LinkedList<>();
        Queue<String> botMessages = botHistory != null ? new LinkedList<>(botHistory.getData()) : new LinkedList<>();

        while (!userMessages.isEmpty() || !botMessages.isEmpty()) {
            if (!userMessages.isEmpty()) {
                requestMessages.add(ChatMessage.builder().role(ChatMessage.Role.USER).content(userMessages.poll()).build());
            }
            if (!botMessages.isEmpty()) {
                requestMessages.add(ChatMessage.builder().role(ChatMessage.Role.ASSISTANT).content(botMessages.poll()).build());
            }
        }
        String fullMessage = "Вопрос: " + userMessage + " Информация о пациенте: " + metaInfo + parametersHistory;
        requestMessages.add(ChatMessage.builder().role(ChatMessage.Role.USER).content(fullMessage).build());
        String response = LLM.talkToChat(requestMessages, 2);
        if ("no".equalsIgnoreCase(response.trim())) {
            return ChatBotAnswers.getRandomMessage();
        }
        return response;
    }

}
