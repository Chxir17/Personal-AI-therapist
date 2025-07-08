package com.aitherapist.aitherapist.interactionWithGigaApi;

import com.aitherapist.aitherapist.db.entities.HealthData;
import com.aitherapist.aitherapist.db.entities.ParserJsonUserHealthData;

import com.aitherapist.aitherapist.db.entities.User;
import com.aitherapist.aitherapist.interactionWithGigaApi.llm.Llm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.ToString;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public final class MakeMedicalRecommendation {



    private static <T> String makeDataList(List<T> values){
        StringBuilder sb = new StringBuilder();
        for (T val : values) {
            sb.append(val).append(", ");
        }
        if (!values.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    public static Map<String, String> MakeMetaInformation(User user) {
        var result = new LinkedHashMap<String, String>();
        result.put("name", makeDataList(List.of(user.getName())));
        result.put("age", makeDataList(List.of(user.getAge() != null ? String.valueOf(user.getAge()) : "null")));
        result.put("male", makeDataList(List.of(user.getMale() != null ? String.valueOf(user.getMale()) : "null")));
        result.put("chronicDiseases", makeDataList(List.of(user.getChronicDiseases() != null ? user.getChronicDiseases() : "null")));
        result.put("height", makeDataList(List.of(user.getHeight() != null ? String.valueOf(user.getHeight()) : "null")));
        result.put("weight", makeDataList(List.of(user.getWeight() != null ? String.valueOf(user.getWeight()) : "null")));
        result.put("badHabits", makeDataList(List.of(user.getBadHabits() != null ? user.getBadHabits() : "null")));
        return result;
    }


    public static Map<String, String> buildMedicalHistory(User user) {
        var result = new LinkedHashMap<String, String>();

        List<HealthData> history = user.getHealthDataList();

        result.put("bloodOxygenLevel", makeDataList(
                history.stream().map(HealthData::getBloodOxygenLevel).toList()
        ));

        result.put("temperature", makeDataList(
                history.stream().map(HealthData::getTemperature).toList()
        ));

        result.put("hoursOfSleepToday", makeDataList(
                history.stream().map(HealthData::getHoursOfSleepToday).toList()
        ));

        result.put("pulse", makeDataList(
                history.stream().map(HealthData::getPulse).toList()
        ));

        result.put("pressure", makeDataList(
                history.stream().map(HealthData::getPressure).toList()
        ));

        result.put("sugar", makeDataList(
                history.stream().map(HealthData::getSugar).toList()
        ));

        result.put("heartPain", makeDataList(
                history.stream().map(HealthData::getHeartPain).toList()
        ));

        result.put("arrhythmia", makeDataList(
                history.stream().map(HealthData::getArrhythmia).toList()
        ));

        return result;
    }

    public static String giveMedicalRecommendation(User user){
        String apiKey = System.getenv("GIGA_CHAT_API_KEY");
        String token = Llm.getGigaChatToken(apiKey);
        Map<String,String> metaInfo = MakeMedicalRecommendation.MakeMetaInformation(user);
        Map<String,String> parametersHistory = MakeMedicalRecommendation.buildMedicalHistory(user);
        String prompt = "Ты — профессиональный врач-терапевт. Твоя задача — проанализировать медицинские данные пациента и написать рекомендацию на русском языке.\n"
                + "Используй следующий стиль:\n"
                + "- в начале кратко обратись к пациенту по имени (если имя есть)\n"
                + "- отметь, какие у пациента хорошие показатели, а какие плохие(критические)(если есть такие данные)\n"
                + "- дай краткий вывод о состоянии здоровья\n"
                + "- дай конкретные рекомендации по разделам (если релевантно): Питание, Активность, Режим дня, Важно в его возрасте\n"
                + "- если какие-либо показатели выше или ниже нормы, прокомментируй это и предложи, что делать\n"
                + "- не превышай объём в ~500 символов, но можешь немного больше, если нужно\n"
                + "Данные пациента:" + metaInfo + parametersHistory
                + "Проанализируй эти данные и составь рекомендацию, как указано выше.";
        String response = Llm.talkToChat(token, prompt);
        return response;
    }

}
