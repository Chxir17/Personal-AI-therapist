package com.aitherapist.aitherapist.telegrambot.utils.createButtons;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InlineKeyboardFactory {

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
        Map<String, String> buttonMap = new LinkedHashMap<>();
        buttonMap.put("📊 Последние данные", "/getLastRecords");
        buttonMap.put("💬 Сообщение пациенту", "/sendMessageToPatient");
        buttonMap.put("⚙️ Настройки", "/settingsDoctor");
        buttonMap.put("📅 Пацинты ", "/doctorPatientsMenu");
        buttonMap.put("📁 История пациентов", "/patientHistory");
        return createInlineKeyboard(buttonMap, 2);
    }

    public static InlineKeyboardMarkup createDoctorSettingsKeyboard() {
        Map<String, String> buttonMap = new LinkedHashMap<>();
        buttonMap.put("✏️ Редактировать профиль", "/changeDoctorAccountData");
        buttonMap.put("🔄 Сменить роль пользователя", "/changeRole");
        buttonMap.put("🔙 Вернуться в главное меню", "/doctorMenu");
        return createInlineKeyboard(buttonMap, 2);
    }

    public static InlineKeyboardMarkup createEditClinicPatientData(){
        Map<String, String> buttons = new LinkedHashMap<>();
        buttons.put("Изменить имя", "/editName");
        buttons.put("Изменить дату рождения", "/editBirthDate");
        buttons.put("Изменить пол", "/editGender");
        buttons.put("Аритмия", "/editArrhythmia");
        buttons.put("Хронические заболевания", "/editChronicDiseases");
        buttons.put("Рост", "/editHeight");
        buttons.put("Вес", "/editWeight");
        buttons.put("Вредные привычки", "/editBadHabits");


        return  InlineKeyboardFactory.createInlineKeyboard(buttons, 2);
    }

    public static InlineKeyboardMarkup createEditDoctorData(){
        Map<String, String> buttons = new LinkedHashMap<>();
        buttons.put("Изменить имя", "/editName");
        buttons.put("Изменить дату рождения", "/editBirthDate");
        buttons.put("Изменить пол", "/editGender");
        return  InlineKeyboardFactory.createInlineKeyboard(buttons, 2);
    }

    public static InlineKeyboardMarkup createPatientManagementKeyboard() {
        Map<String, String> buttonMap = new LinkedHashMap<>();
        buttonMap.put("➕ Добавить пациента", "/addPatient");
        buttonMap.put("👥 Список пациентов", "/patientList");
        buttonMap.put("ℹ️ Информация о пациенте", "/patientInfo");
        return createInlineKeyboard(buttonMap, 2);
    }

    public static InlineKeyboardMarkup createPatientDefaultKeyboard() {
        Map<String, String> buttonMap = new LinkedHashMap<>();
        buttonMap.put("📊 Ввести daily данные", "/inputDailyData");
        buttonMap.put("💬 Написать доктору", "/writeToDoctor");
        buttonMap.put("👤 Мой профиль", "/myProfile");
        buttonMap.put("⚙️ Настройки", "/patientSettings");
        buttonMap.put("📈 История показателей", "/myHealthHistory");
        return createInlineKeyboard(buttonMap, 2);
    }


}