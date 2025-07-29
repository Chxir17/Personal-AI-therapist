package com.aitherapist.aitherapist.functionality.QAChatBot;

import chat.giga.model.completion.ChatMessage;
import com.aitherapist.aitherapist.domain.enums.ChatBotAnswers;
import com.aitherapist.aitherapist.domain.enums.HypertensionQA;
import com.aitherapist.aitherapist.domain.enums.Prompts;
import com.aitherapist.aitherapist.domain.model.entities.History;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.interactionWithGigaApi.utils.LLM;
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

        String articleTitle = findArticleTitleWithLLM(userMessage);
        if (!"no".equalsIgnoreCase(articleTitle)) {
            HypertensionQA article = HypertensionQA.findByQuestion(articleTitle);
            if (article != null) {
                return article.getAnswer() + "\nТакже эта статья может быть полезна: " +
                        article.getQuestion() + "\n" + article.getAuthor();
            }
        }

        Map<String, String> metaInfo = patient.makeMetaInformation(patient);
        Map<String, String> parametersHistory = patient.buildMedicalHistory();
        String systemPrompt = Prompts.valueOf("CHAT_BOT_PROMPT").getMessage();

        List<ChatMessage> requestMessages = new ArrayList<>();
        requestMessages.add(ChatMessage.builder().role(ChatMessage.Role.SYSTEM).content(systemPrompt).build());

        List<History> safeHistoryList = historyList != null ? historyList : Collections.emptyList();

        History userHistory = safeHistoryList.stream()
                .filter(Objects::nonNull)
                .filter(h -> Boolean.TRUE.equals(h.getRole()))
                .findFirst().orElse(null);

        History botHistory = safeHistoryList.stream()
                .filter(Objects::nonNull)
                .filter(h -> Boolean.FALSE.equals(h.getRole()))
                .findFirst().orElse(null);

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

        String rawResponse = llm.talkToChat(requestMessages, 3).trim();
        if ("no".equalsIgnoreCase(rawResponse)) {
            return ChatBotAnswers.getRandomMessage();
        }
        return rawResponse;
    }

    private String findArticleTitleWithLLM(String userMessage) {
        String prompt = Prompts.valueOf("FIND_ARTICLE_PROMPT").getMessage();

        List<ChatMessage> request = List.of(
                ChatMessage.builder().role(ChatMessage.Role.SYSTEM).content(prompt).build(),
                ChatMessage.builder().role(ChatMessage.Role.USER).content(userMessage).build()
        );

        try {
            return llm.talkToChat(request, 2).trim();
        } catch (Exception e) {
            return "no";
        }
    }
}
