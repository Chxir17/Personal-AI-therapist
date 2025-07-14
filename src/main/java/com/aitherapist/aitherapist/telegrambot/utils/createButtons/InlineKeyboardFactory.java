package com.aitherapist.aitherapist.telegrambot.utils.createButtons;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InlineKeyboardFactory {

    /**
     * Создает клавиатуру из Map: ключ — текст кнопки, значение — callbackData
     *
     * @param buttonMap отображение текста кнопок на callback data
     * @param buttonsPerRow сколько кнопок в одном ряду (по умолчанию 1, если < 1)
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup createInlineKeyboard(Map<String, String> buttonMap, int buttonsPerRow) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        if (buttonsPerRow < 1) buttonsPerRow = 1;

        List<InlineKeyboardButton> currentRow = new ArrayList<>();
        for (Map.Entry<String, String> entry : buttonMap.entrySet()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(entry.getKey());
            button.setCallbackData(entry.getValue());

            currentRow.add(button);
            if (currentRow.size() == buttonsPerRow) {
                keyboard.add(currentRow);
                currentRow = new ArrayList<>();
            }
        }

        if (!currentRow.isEmpty()) {
            keyboard.add(currentRow);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

    public static InlineKeyboardMarkup createDoctorDefaultKeyboard() {
        Map<String, String> buttonMap = new LinkedHashMap<>(); // сохраняем порядок
        buttonMap.put("📊 Последние данные", "/getLastRecords");
        buttonMap.put("💬 Сообщение пациенту", "/sendMessageToPatient");
        buttonMap.put("⚙️ Настройки", "/settingsDoctor");

        // ➕ Новые кнопки:
        buttonMap.put("📅 Запись на приём", "/scheduleAppointment");
        buttonMap.put("📁 История пациентов", "/patientHistory");

        return createInlineKeyboard(buttonMap, 2);
    }
}
