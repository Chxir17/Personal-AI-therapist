package com.aitherapist.aitherapist.functionality.QAChatBot;

import chat.giga.model.completion.ChatMessage;
import com.aitherapist.aitherapist.domain.enums.ChatBotAnswers;
import com.aitherapist.aitherapist.domain.enums.Prompts;
import com.aitherapist.aitherapist.domain.model.entities.History;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.interactionWithGigaApi.utils.LLM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserQuestions {

    private final LLM llm;

    @Autowired
    public UserQuestions(LLM llm) {
        this.llm = llm;
    }

    public String answerUserQuestion(Patient patient, String userMessage, List<History> historyList) {
        // Проверка на null для patient
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }

        Map<String, String> metaInfo = patient.makeMetaInformation(patient);
        Map<String, String> parametersHistory = patient.buildMedicalHistory();
        Prompts prompt = Prompts.valueOf("CHAT_BOT_PROMPT");
        String systemPrompt = prompt.getMessage();

        List<ChatMessage> requestMessages = new ArrayList<>();
        requestMessages.add(ChatMessage.builder().role(ChatMessage.Role.SYSTEM).content(systemPrompt).build());

        // Безопасная обработка historyList
        List<History> safeHistoryList = historyList != null ? historyList : Collections.emptyList();

        // Фильтрация с проверкой на null
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

        // Создание безопасных очередей
        Queue<String> userMessages = userHistory != null && userHistory.getData() != null
                ? new LinkedList<>(userHistory.getData())
                : new LinkedList<>();

        Queue<String> botMessages = botHistory != null && botHistory.getData() != null
                ? new LinkedList<>(botHistory.getData())
                : new LinkedList<>();

        // Добавление сообщений в запрос
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

        String response = llm.talkToChat(requestMessages, 2);
        if ("no".equalsIgnoreCase(response.trim())) {
            return ChatBotAnswers.getRandomMessage();
        }
        return response;
    }
}