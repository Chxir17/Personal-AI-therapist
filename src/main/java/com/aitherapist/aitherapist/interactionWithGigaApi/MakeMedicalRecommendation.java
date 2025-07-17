package com.aitherapist.aitherapist.interactionWithGigaApi;

import chat.giga.model.completion.ChatMessage;
import com.aitherapist.aitherapist.domain.enums.HypertensionQA;
import com.aitherapist.aitherapist.domain.enums.Prompts;
import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.interactionWithGigaApi.llm.Llm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.dialect.unique.CreateTableUniqueDelegate;

import java.util.*;

public class MakeMedicalRecommendation {
    public static String giveMedicalRecommendation(Patient patient){
        String token = Llm.getGigaChatToken();
        Map<String,String> metaInfo = patient.makeMetaInformation(patient);
        Map<String,String> parametersHistory = patient.buildMedicalHistory();
        Prompts prompt = Prompts.valueOf("RECOMMENDATION_PROMPT");
        String systemPrompt = prompt.getMessage();
        String userMessage = metaInfo.toString() + parametersHistory;
        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );
        return Llm.talkToChat(token, requestMessage);
    }

    public static String giveMedicalRecommendationBeta(Patient patient) {
        try {
            String token = Llm.getGigaChatToken();
            Map<String, String> metaInfo = patient.makeMetaInformation(patient);
            Map<String, String> parametersHistory = patient.buildMedicalHistory();
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> userPrompt = new HashMap<>();
            userPrompt.put("metaInfo", metaInfo);
            userPrompt.put("parametersHistory", parametersHistory);

            String userMessage = mapper.writeValueAsString(userPrompt);
            Prompts prompt = Prompts.valueOf("RECOMMENDATION_BETA_PROMPT");
            String systemPrompt = prompt.getMessage();

            List<ChatMessage> requestMessage = Arrays.asList(
                    ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                    ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
            );
            return getFullRecommendation(Llm.talkToChat(token, requestMessage, 3));
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String giveMedicalRecommendationWithScoreBeta(ClinicPatient patient) {
        try {
            String token = Llm.getGigaChatToken();
            Map<String, String> metaInfo = patient.makeMetaInformation(patient);
            Map<String, String> parametersHistory = patient.buildMedicalHistory();
            Map<String, String> goals = patient.buildGoalsInformation();
            // Печать metaInfo
            System.out.println("Meta Information:");
            for (Map.Entry<String, String> entry : metaInfo.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

            // Печать parametersHistory
            System.out.println("\nParameters History:");
            for (Map.Entry<String, String> entry : parametersHistory.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

            // Печать goals
            System.out.println("\nGoals Information:");
            for (Map.Entry<String, String> entry : goals.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> userPrompt = new HashMap<>();
            userPrompt.put("metaInfo", metaInfo);
            userPrompt.put("parametersHistory", parametersHistory);
            userPrompt.put("heath_goals", goals);
            String userMessage = mapper.writeValueAsString(userPrompt);
            Prompts prompt = Prompts.valueOf("RECOMMENDATION_SCORE_BETA_PROMPT");
            String systemPrompt = prompt.getMessage();
            List<ChatMessage> requestMessage = Arrays.asList(
                    ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                    ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
            );
            return getFullRecommendation(Llm.talkToChat(token, requestMessage, 2));
        }
        catch (Exception e) {
            return null;
        }
    }

    private static String getFullRecommendation(String jsonResponse){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            String recommendation = jsonNode.get("text").asText();
            String articleTitle = jsonNode.get("article").asText();
            if (articleTitle != null && !articleTitle.isBlank()) {
                HypertensionQA article = HypertensionQA.findByQuestion(articleTitle);
                if (article != null) {
                    String articleText = article.getAnswer();
                    return recommendation + "\nЭта статья может быть полезна: " + articleTitle + "\n" + articleText;
                }
            }
            else {
                return recommendation;
            }
        }
        catch (Exception e){
            return null;
        }
        return null;
    }
}
