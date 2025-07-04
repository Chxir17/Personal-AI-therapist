package com.aitherapist.aitherapist.telegrambot.utils;

public enum Answers {
    START_MESSAGE("Привет! Я ваш телеграм-бот. Чем могу помочь?"),
    INFORMATION_MESSAGE("""
        Пожалуйста, введите следующие медицинские параметры через запятую в одном сообщении:
        
        • Температура тела (в °C, например 36.6)
        • Пульс (ударов в минуту, например 72)
        • Боли в сердце (да/нет)
        • Боли в животе (да/нет)
        • Уровень кислорода в крови (SpO2 в %, например 98)
        
        Пример корректного ввода:
        37.2, 75, нет, да, 96
        """),
    IS_NOT_MEDICAL_INFORMATION("Извините, это не медицинская информация! По таким " +
            "вопросам лучше общаться напрямую к Giga Chat");

    private final String message;

    Answers(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}