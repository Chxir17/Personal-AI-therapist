package com.aitherapist.aitherapist.interactionWithGigaApi;

import com.aitherapist.aitherapist.interactionWithGigaApi.llm.Llm;

public class ParseUserPrompt {


    public String initPromptParser(String userPrompt) throws InterruptedException {
        String apiKey = System.getenv("GIGA_CHAT_API_KEY");
        String token = Llm.getGigaChatToken(apiKey);
        String systemPrompt = """ 
    "Parse user messages and extract personal health-related information in the following format. If any field is missing or not mentioned, use null as its value. Output only a valid JSON without any explanation or extra text.",
    "user": {
        "name": str | null,
        "age": int | null,
        "male": bool | null,  # true if male, false if female, null if not mentioned
        "chronicDiseases": str | null,
        "height": double | null,  # in centimeters
        "weight": double | null,  # in kilograms
        "badHabits": str | null
    }
    User Message:
""";
        String finalPrompt = systemPrompt + userPrompt;
//        String response = Llm.talkToChat(token, finalPrompt);
        String response = "";
        for (int i = 0; i < 10; i++) {
            try {
                response = Llm.talkToChat(token, finalPrompt);
                break;
            } catch (Exception e) {
                if (i == 10 - 1) throw e;
                Thread.sleep(1000);
            }
        }
        return response;
    }

    public static String dailyQuestionnaireParser(String userPrompt){
        String apiKey = System.getenv("GIGA_CHAT_API_KEY");
        String token = Llm.getGigaChatToken(apiKey);
        String systemPrompt = """
"your role is": "You are a medical assistant. Parse user messages and extract the following physiological data. If any field is missing or not mentioned, use null as its value. Output only a valid JSON without any explanation or extra text.",
"user": {
    "bloodOxygenLevel": float | null,      // in percentage (e.g., 98.50)
    "temperature": float | null,           // in Celsius (e.g., 36.6)
    "hoursOfSleepToday": float | null,     // in hours (e.g., 7.5)
    "pulse": int | null,                   // heart rate in beats per minute
    "pressure": float | null,              // arterial pressure (e.g., 120.75)
    "sugar": float | null,                 // blood sugar level (e.g., 5.60)
    "heartPain": bool | null,              // true if the user reports chest or heart pain
    "arrhythmia": bool | null              // true if the user reports arrhythmia
}
User Message:
""";
        String finalPrompt = systemPrompt+ userPrompt;
        String response = Llm.talkToChat(token, finalPrompt);

        return response;
    }

    public void main(String[] args) throws InterruptedException {
        String userPrompt = "Hi, I'm Alex. I'm 34, male. I have asthma. I smoke. Height 182 cm, weight 76 kg.";

        String response = initPromptParser(userPrompt);

        System.out.println(response);
    }
}
