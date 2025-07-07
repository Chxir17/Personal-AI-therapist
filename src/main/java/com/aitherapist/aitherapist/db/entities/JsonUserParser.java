package com.aitherapist.aitherapist.db.entities;

import com.aitherapist.aitherapist.db.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUserParser {

    public static User extractUserFromGigaResponse(String rawResponse) {
        try {
            // Ищем JSON-объект внутри строки
            Pattern pattern = Pattern.compile("\\{.*?\\}", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(rawResponse);

            if (matcher.find()) {
                String json = matcher.group(0); // захватываем только JSON часть
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(json, User.class);
            } else {
                throw new RuntimeException("Не удалось найти JSON в ответе: " + rawResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при парсинге ответа GigaChat", e);
        }
    }
}
