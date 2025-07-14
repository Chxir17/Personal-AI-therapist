package com.aitherapist.aitherapist.interactionWithGigaApi;

import chat.giga.model.completion.ChatMessage;
import com.aitherapist.aitherapist.domain.model.entities.HealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.interactionWithGigaApi.llm.Llm;
import java.util.*;
import com.aitherapist.aitherapist.domain.model.entities.User;
public class MakeMedicalRecommendation {



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
        result.put("male", makeDataList(List.of(user.getGender() != null ? String.valueOf(user.getGender()) : "null")));
        return result;
    }

    public static Map<String, String> buildMedicalHistory(Patient patient) {
        var result = new LinkedHashMap<String, String>();

        List<HealthData> history = patient.getHealthDataList();

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

        result.put("heartPain", makeDataList(
                history.stream().map(HealthData::getHeartPain).toList()
        ));

        result.put("arrhythmia", makeDataList(
                history.stream().map(HealthData::getArrhythmia).toList()
        ));

        return result;
    }

    public static String giveMedicalRecommendation(Patient patient){
        String token = Llm.getGigaChatToken();
        Map<String,String> metaInfo = MakeMedicalRecommendation.MakeMetaInformation(patient);
        Map<String,String> parametersHistory = MakeMedicalRecommendation.buildMedicalHistory(patient);
        String systemPrompt = """
                [Роль для модели]: Ты — опытный терапевт.
                [Задача]: Проанализируй медицинские данные пациента и напиши ему личную рекомендацию по улучшению здоровья. В рекомендациях используй формальный стиль обращения, акцентируй внимание на сильных и слабых сторонах здоровья пациента, давай советы по основным аспектам жизни (питание, активность, режим дня). Сообщение должно быть структурировано и не превышать 800 символов без пробелов.
                [Инструкция]:
                1. Начни обращение с имени пациента.
                2. Проанализируй представленные медицинские показатели, отмечая те, которые находятся в норме, и те, которые требуют внимания.
                3. Сделай краткий обзор общего состояния здоровья.
                4. Дай персонализированные рекомендации по таким категориям, как питание, физическая активность, распорядок дня и возрастные особенности.
                5. Если значения отклоняются от нормы, подробно объясни причины и предложи корректирующие действия.
                6. Обязательно придерживайся указанного лимита по символам.
                [Формат ответа]:
                Пациенту [Имя],
                [Текст рекомендации]
                [Примеры желаемых и нежелательных ответов отсутствуют, так как требуются конкретные медицинские данные].
                [Примечания]: Убедись, что рекомендации четко адресованы конкретному человеку и соответствуют его возрасту и состоянию здоровья.
                [Критерии качества]:
                - Четкость и однозначность рекомендаций.
                - Адекватный учет индивидуальных особенностей пациента.
                - Корректность медицинских комментариев и предложений.
                - Соблюдение ограничений по длине текста.""";
        String userMessage = metaInfo.toString() + parametersHistory;
        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );
        return Llm.talkToChat(token, requestMessage);
    }

}
