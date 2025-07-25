package com.aitherapist.aitherapist.functionality.recommendationSystem;

import chat.giga.model.completion.ChatMessage;
import com.aitherapist.aitherapist.domain.enums.HypertensionQA;
import com.aitherapist.aitherapist.domain.enums.Prompts;
import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.interactionWithGigaApi.utils.LLM;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class MakeMedicalRecommendation {

    private final LLM llm;

    @Autowired
    public MakeMedicalRecommendation(LLM llm) {
        this.llm = llm;
    }

    public String giveMedicalRecommendation(Patient patient){
        Map<String,String> metaInfo = patient.makeMetaInformation(patient);
        Map<String,String> parametersHistory = patient.buildMedicalHistory();
        Prompts prompt = Prompts.valueOf("RECOMMENDATION_PROMPT");
        String systemPrompt = prompt.getMessage();
        String userMessage = metaInfo.toString() + parametersHistory;
        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );
        return llm.talkToChat(requestMessage);
    }

    public String giveMedicalRecommendationBeta(Patient patient) {
        try {
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
            return llm.talkToChat(requestMessage, 3);
        }
        catch (Exception e) {
            return null;
        }
    }

    public String giveMedicalRecommendationWithScoreBeta(ClinicPatient patient) {
        try {
            Map<String, String> metaInfo = patient.makeMetaInformation(patient);
            Map<String, String> parametersHistory = patient.buildMedicalHistory();
            Map<String, String> goals = patient.buildGoalsInformation();
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
            return llm.talkToChat(requestMessage, 3);
        }
        catch (Exception e) {
            return null;
        }
    }
}
