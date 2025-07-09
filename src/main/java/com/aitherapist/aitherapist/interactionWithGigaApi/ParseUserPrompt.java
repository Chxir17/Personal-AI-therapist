package com.aitherapist.aitherapist.interactionWithGigaApi;

import chat.giga.model.completion.ChatMessage;
import com.aitherapist.aitherapist.interactionWithGigaApi.llm.Llm;

import java.util.Arrays;
import java.util.List;

public class ParseUserPrompt {


    public static String initPromptParser(String userMessage) throws InterruptedException {
        String token = Llm.getGigaChatToken();
        String systemPrompt = """
[Роль для модели]: Ты — помощник, способный извлекать личную и медицинскую информацию из текстовых сообщений пользователей и представлять её в виде корректного JSON-объекта.

[Задача]: Проанализируй сообщение пользователя и заполни следующие поля информацией, представленной в сообщении:
- "name" — имя пользователя (строка)
- "age" — возраст пользователя (целое число)
- "gender" — пол пользователя (true если мужчина; false если женщина)
- "chronicDiseases" — хронические заболевания (строка)
- "height" — рост пользователя в сантиметрах (дробное число)
- "weight" — вес пользователя в килограммах (дробное число)
- "badHabits" — вредные привычки (строка) Преобразуй описания вредных привычек пользователя в словосочетания с глаголом в форме третьего лица единственного числа.

[Инструкция]:
1. Если пол человека не указан явно, определи его по грамматическим маркерам в тексте (например, окончаниям глаголов и причастий в прошедшем времени: "-лся" или "родился" — мужчина, "-лась" или "родилась" — женщина). Также используй контекстные местоимения и слова (например, "я родилась" — женщина, "я родился" — мужчина).
2. Если по грамматике пол определить нельзя, попробуй определить пол по имени, учитывая распространённость имени для мужчин или женщин.
3. Если и по имени пол определить нельзя или есть неоднозначность, ставь значение null.
4. Обязательно учитывай указанные типы данных. Например, если возраст указан строкой ("20 лет"), преобразуй его в целое число (20), а если указана единица измерения, которая не соответствует указанной (например, "вес 150 фунтов"), преобразуй значение в требуемые единицы измерения.
5. Игнорируй любые дополнительные сведения, которые не подходят под один из указанных параметров.

[Дополнительные уточнения]:
- Не считай обычные или полезные привычки вредными. Например, «пью много воды», «занимаюсь спортом», «люблю гулять» — это не вредные привычки и не должны заполняться в поле "badHabits". Вредные привычки — это только те, которые могут нанести вред здоровью (например, курение, злоупотребление алкоголем, наркотиками, переедание, злоупотребление сладким и т.д.).
[Формат ответа]:
{"name": <string>, "age": <int> | null, "gender": <bool> | null, "chronicDiseases": <string> | null, "height": <double> | null, "weight": <double> | null, "badHabits": <string> | null}

[Примечания]:
- Используй строгую обработку данных согласно указанным типам и единицам измерений.
- Вывод должен быть только в виде валидного JSON без комментариев или пояснений.

[Критерии качества]:
- Корректное заполнение всех полей JSON согласно правилам преобразования данных.
- Использование null для незаполненных или неправильно представленных данных.
- Строгое соблюдение типов данных, систем измерения и формата вывода.
- Игнорирование любой информации, не относящейся к заданным полям.
""";

        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );
        String response = "";
        for (int i = 0; i < 10; i++) {
            try {
                response = Llm.talkToChat(token, requestMessage);
                break;
            } catch (Exception e) {
                if (i == 10 - 1) throw e;
                Thread.sleep(1000);
            }
        }
        return response;
    }

    public static String dailyQuestionnaireParser(String userMessage){
        String token = Llm.getGigaChatToken();
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
        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );
        String response = Llm.talkToChat(token, requestMessage);

        return response;
    }

    public static void main(String[] args) throws InterruptedException {
        String userPrompt = "Я Саша мне 19 я родилась в России я могу подтянутся 15 раз и еще я много пью пива. мой рост 197 вес 70000 г";

        String response = initPromptParser(userPrompt);

        System.out.println(response);
    }
}
