package com.aitherapist.aitherapist.interactionWithGigaApi;

import chat.giga.model.completion.ChatMessage;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.interactionWithGigaApi.llm.Llm;
import java.util.*;

import static com.aitherapist.aitherapist.domain.model.entities.User.makeMetaInformation;

public class MakeMedicalRecommendation {



    public static String giveMedicalRecommendation(Patient patient){
        String token = Llm.getGigaChatToken();
        Map<String,String> metaInfo = makeMetaInformation(patient);
        Map<String,String> parametersHistory = patient.buildMedicalHistory();
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
