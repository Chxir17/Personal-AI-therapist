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
- "name" — имя пользователя
- "age" — возраст пользователя
- "gender" — пол пользователя (true если мужчина; false если женщина)
- "chronicDiseases" — хронические заболевания
- "height" — рост пользователя в сантиметрах
- "weight" — вес пользователя в килограммах
- "badHabits" — вредные привычки.

[Инструкция]:
1. Если пол человека не указан явно, определи его по грамматическим маркерам в тексте (например, окончаниям глаголов и причастий в прошедшем времени: "-лся" или "родился" — мужчина, "-лась" или "родилась" — женщина). Также используй контекстные местоимения и слова (например, "я родилась" — женщина, "я родился" — мужчина).
2. Если по грамматике пол определить нельзя, попробуй определить пол по имени, учитывая распространённость имени для мужчин или женщин.
3. Если и по имени пол определить нельзя или есть неоднозначность, ставь значение null.
4. Преобразуй описания вредных привычек пользователя в словосочетания с глаголом в форме третьего лица единственного числа.
5. Обязательно учитывай указанные типы данных. Например, если возраст указан строкой ("20 лет"), преобразуй его в целое число (20), а если указана единица измерения, которая не соответствует указанной (например, "вес 150 фунтов"), преобразуй значение в требуемые единицы измерения.
6. Игнорируй любые дополнительные сведения, которые не подходят под один из указанных параметров.

[Дополнительные уточнения]:
- Не считай обычные или полезные привычки вредными. «пью много воды», «занимаюсь спортом», «люблю гулять» — это не вредные привычки и не должны заполняться в поле "badHabits". Вредные привычки — это только те, которые могут нанести вред здоровью (например, курение, злоупотребление алкоголем, наркотиками, переедание, злоупотребление сладким и т.д.).
- Заносить в форму значения на том языке на котором они написаны пользователем.
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
[Роль для модели]:
Ты — помощник, способный извлекать личную и медицинскую информацию из текстовых сообщений пользователей и представлять её в виде корректного JSON-объекта.

[Задача]:
Проанализируй сообщение пользователя и заполни следующие поля данными, которые упоминаются в сообщении:
"bloodOxygenLevel" — уровень кислорода в крови в процентах (float)
"temperature" — температура тела в градусах Цельсия (float)
"hoursOfSleepToday" — количество часов сна за сегодняшний день (float)
"pulse" — пульс в ударах в минуту (int)
"pressure" — артериальное давление (string в формате "число/число")
"sugar" — уровень сахара в крови в ммоль/л (float)
"heartPain" — наличие боли в области сердца или груди (true, если есть; false — если отсутствует)
"arrhythmia" — наличие аритмии (true, если есть; false — если отсутствует)

[Инструкция]:
1.Если параметр явно указан в сообщении, извлеки его значение и запиши в JSON в соответствующем типе данных и единицах измерения.
2.Если пользователь пишет, что какой-либо параметр нормальный, в норме, нормальные, нормальное, в порядке, всё хорошо, без отклонений, в идеале или использует синонимичные фразы, замени это на среднее значение для соответствующего параметра:
bloodOxygenLevel: 98.0
temperature: 36.6
hoursOfSleepToday: 8.0
pulse: 70
pressure: "120/80"
sugar: 5.5
heartPain: false
arrhythmia: false
3.Если пользователь называет единицы измерения, отличные от требуемых, выполни конвертацию.
4.Если параметр в сообщении не упоминается, запиши null в соответствующее поле.
5.Игнорируй любые другие сведения, которые не относятся к перечисленным параметрам.


[Формат ответа]:
Выводи только валидный JSON без комментариев или пояснений, строго в следующей структуре и с строго указанными типами данных:
{"bloodOxygenLevel": <float> | null, "temperature": <float> | null, "hoursOfSleepToday": <float> | null, "pulse": <int> | null, "pressure": <string(int/int)> | null, "sugar": <float> | null, "heartPain": <bool> | null, "arrhythmia": <bool> | null}

[Примечания]:
1.Никогда не оставляй в значениях полей данные отличные от указанных типов и форматов указанных выше.
2.Строго соблюдай типы данных.
3.Игнорируй параметры, которых нет в списке.

[Критерии качества]
- Корректное извлечение значений из текста и преобразование их в нужные единицы измерения и типы данных.
- Использование null для отсутствующих данных.
- Строгое соблюдение формата JSON и типов данных.
- Игнорирование всей информации, не относящейся к указанным параметрам.
""";

        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );
        String response = Llm.talkToChat(token, requestMessage);

        return response;
    }

    public static void main(String[] args) throws InterruptedException {
        String userPrompt1 = "Я Саша мне 19 я родился в России много подтягиваюсь на турнике и еще я много пью колу. мой рост 197 вес 70000 г";
        String userPrompt2 = "I'am Sasha and I'm 19. I was born in Russia. I love do push ups and also i drink a lot cola. My parameters is 197 and 70";
        String userPrompt = "давление и пульс нормальные. температура 38 и понос чуть-чуть чо мне делать???";
        String userHealthData = "";
//        String response = initPromptParser(userPrompt);
        String response = dailyQuestionnaireParser(userPrompt);
        System.out.println(response);
    }
}
