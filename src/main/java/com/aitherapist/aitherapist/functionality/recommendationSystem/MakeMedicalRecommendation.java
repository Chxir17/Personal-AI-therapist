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
import java.util.regex.Pattern;


@Component
public class MakeMedicalRecommendation {

    private final LLM llm;

    @Autowired
    public MakeMedicalRecommendation(LLM llm) {
        this.llm = llm;
    }

    public static String removeForbiddenTags(String input) {
        String[] forbiddenTags = {"br", "p", "div", "span"};
        String pattern = "</?(" + String.join("|", forbiddenTags) + ")(\\s[^>]*)?>";
        return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(input).replaceAll("");
    }


    public String giveMedicalRecommendationWithScore(Patient patient) {
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
            Prompts prompt = Prompts.valueOf("RECOMMENDATION_SCORE_PROMPT");
            String systemPrompt = prompt.getMessage();
            List<ChatMessage> requestMessage = Arrays.asList(
                    ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                    ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
            );
            return removeForbiddenTags(llm.talkToChat(requestMessage, 3));
        }
        catch (Exception e) {
            return null;
        }
    }
}
