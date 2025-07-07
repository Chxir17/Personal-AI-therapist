package com.aitherapist.aitherapist.interactionWithGigaApi;

import com.aitherapist.aitherapist.db.entities.ParserJsonUserHealthData;

import com.aitherapist.aitherapist.interactionWithGigaApi.llm.Llm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MakeMedicalRecomendation {

    public static  String makeMedicalRecomendation(String jsonMedicalData) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ParserJsonUserHealthData user = mapper.readValue(jsonMedicalData, ParserJsonUserHealthData.class);
        String apiKey = System.getenv("GIGA_CHAT_API_KEY");
        String token = Llm.getGigaChatToken(apiKey);
        String prompt = "Here is patient medical data: bloodOxygenLevel " + user.getBloodOxygenLevel()+", temperature "+user.getTemperature()+
                ", hoursOfSleepToday " + user.getHoursOfSleepToday()+", pulse "+user.getPulse() + ", pressure "+ user.getPressure()+
                ", sugar "+ user.getSugar() + ", heartPain "+ user.getHeartPain()+", arrhythmia "+user.getArrhythmia() + ". \n Make some medical recommendation according data which i send you";
        String response = Llm.talkToChat(token, prompt);
        return response;
    }

}
