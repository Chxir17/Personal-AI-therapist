package com.aitherapist.aitherapist.functionality.QAChatBot;

import chat.giga.model.completion.ChatMessage;
import com.aitherapist.aitherapist.domain.enums.ChatBotAnswers;
import com.aitherapist.aitherapist.domain.enums.HypertensionQA;
import com.aitherapist.aitherapist.domain.enums.Prompts;
import com.aitherapist.aitherapist.domain.model.entities.History;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.interactionWithGigaApi.utils.LLM;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserQuestions {

    private final LLM llm;

    @Autowired
    public UserQuestions(LLM llm) {
        this.llm = llm;
    }

    public String answerUserQuestion(Patient patient, String userMessage, List<History> historyList) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }

        Map<String, String> metaInfo = patient.makeMetaInformation(patient);
        Map<String, String> parametersHistory = patient.buildMedicalHistory();
        Prompts prompt = Prompts.valueOf("CHAT_BOT_PROMPT");
        String systemPrompt = prompt.getMessage();

        List<ChatMessage> requestMessages = new ArrayList<>();
        requestMessages.add(ChatMessage.builder().role(ChatMessage.Role.SYSTEM).content(systemPrompt).build());

        List<History> safeHistoryList = historyList != null ? historyList : Collections.emptyList();

        History userHistory = safeHistoryList.stream()
                .filter(Objects::nonNull)
                .filter(h -> Boolean.TRUE.equals(h.getRole()))
                .findFirst()
                .orElse(null);

        History botHistory = safeHistoryList.stream()
                .filter(Objects::nonNull)
                .filter(h -> Boolean.FALSE.equals(h.getRole()))
                .findFirst()
                .orElse(null);

        Queue<String> userMessages = userHistory != null && userHistory.getData() != null
                ? new LinkedList<>(userHistory.getData()) : new LinkedList<>();

        Queue<String> botMessages = botHistory != null && botHistory.getData() != null
                ? new LinkedList<>(botHistory.getData()) : new LinkedList<>();

        while (!userMessages.isEmpty() || !botMessages.isEmpty()) {
            if (!userMessages.isEmpty()) {
                requestMessages.add(ChatMessage.builder()
                        .role(ChatMessage.Role.USER)
                        .content(userMessages.poll())
                        .build());
            }
            if (!botMessages.isEmpty()) {
                requestMessages.add(ChatMessage.builder()
                        .role(ChatMessage.Role.ASSISTANT)
                        .content(botMessages.poll())
                        .build());
            }
        }

        String fullMessage = "Вопрос: " + userMessage + " Информация о пациенте: " + metaInfo + parametersHistory;
        requestMessages.add(ChatMessage.builder()
                .role(ChatMessage.Role.USER)
                .content(fullMessage)
                .build());

        String rawResponse = llm.talkToChat(requestMessages, 2).trim();
        System.out.println("RAWRESPONSE" + rawResponse);
        if ("no".equalsIgnoreCase(rawResponse)) {
            return ChatBotAnswers.getRandomMessage();
        }

        String result = parseJsonAnswer(rawResponse);
        return result != null ? result : ChatBotAnswers.getRandomMessage();
    }

    private String parseJsonAnswer(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);
            String text = root.has("text") ? root.get("text").asText() : null;
            String articleTitle = root.has("article") ? root.get("article").asText() : "no";

            if (text == null || text.isBlank()) {
                return null;
            }

            if (!"no".equalsIgnoreCase(articleTitle)) {
                HypertensionQA article = HypertensionQA.findByQuestion(articleTitle);
                if (article != null) {
                    return text + "\nТакже эта статья может быть полезна: " + articleTitle + "\n" +
                            article.getAnswer() + "\n" + article.getAuthor();
                }
            }
            return text;

        } catch (Exception e) {
            return null;
        }
    }
}
