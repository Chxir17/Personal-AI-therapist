package com.aitherapist.aitherapist.telegrambot.utils.createButtons;

import com.aitherapist.aitherapist.domain.model.entities.Patient;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

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
        buttonMap.put("💬 Сообщение пациенту", "/sendMessageToPatient");
        buttonMap.put("⚙️ Настройки", "/settingsDoctor");
        buttonMap.put("📅 Пацинты ", "/doctorPatientsMenu");
        buttonMap.put("👤 Мой профиль", "/DoctorProfile");
        return createInlineKeyboard(buttonMap, 2);
    }

    public static InlineKeyboardMarkup createDoctorSettingsKeyboard() {
        Map<String, String> buttonMap = new LinkedHashMap<>();
        buttonMap.put("✏️ Редактировать профиль", "/editDoctorAccountData");
        buttonMap.put("🔄 Сменить роль пользователя", "/changeRole");
        buttonMap.put("🔙 Вернуться в главное меню", "/doctorMenu");
        return createInlineKeyboard(buttonMap, 2);
    }

    public static InlineKeyboardMarkup createPatientSettingsKeyboard() {
        Map<String, String> buttonMap = new LinkedHashMap<>();
        buttonMap.put("✏️ Редактировать профиль", "/editPatientAccountData");
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
        return createInlineKeyboard(buttonMap, 2);
    }

    public static InlineKeyboardMarkup createPatientDefaultKeyboard() {
        Map<String, String> buttonMap = new LinkedHashMap<>();
        buttonMap.put("📊 Ввести ежедневные данные", "/inputDailyData");
        buttonMap.put("💬 Написать доктору", "/writeToDoctor");
        buttonMap.put("👤 Мой профиль", "/myProfile");
        buttonMap.put("⚙️ Настройки", "/patientSettings");
        buttonMap.put("📈 История показателей", "/myHealthHistory");
        return createInlineKeyboard(buttonMap, 2);
    }

    public static InlineKeyboardMarkup createAcceptOrEditKeyboardClinic() {
        Map<String, String> buttons = new LinkedHashMap<>();
        buttons.put("✅ Принять", "/acceptInitDataClinic");
        buttons.put("✏️ Изменить", "/editParameters");
        return createInlineKeyboard(buttons, 2);
    }

    public static InlineKeyboardMarkup createAcceptOrEditKeyboard() {
        Map<String, String> buttons = new LinkedHashMap<>();
        buttons.put("✅ Принять", "/acceptInitData");
        buttons.put("✏️ Изменить", "/editParameters");
        return createInlineKeyboard(buttons, 2);
    }

    public static InlineKeyboardMarkup createDoctorProfileKeyboard() {
        return new InlineKeyboardMarkup(List.of(
                List.of(
                        InlineKeyboardButton.builder()
                                .text("📊 Мои пациенты")
                                .callbackData("doctor_patients_list")
                                .build()
                ),
                List.of(
                        InlineKeyboardButton.builder()
                                .text("✏️ Редактировать профиль")
                                .callbackData("edit_doctor_profile")
                                .build(),
                        InlineKeyboardButton.builder()
                                .text("🔙 Назад")
                                .callbackData("back_to_doctor_menu")
                                .build()
                )
        ));
    }

    public static InlineKeyboardMarkup createPatientsKeyboard(List<Patient> patients) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Patient patient : patients) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("💬 " + patient.getName());
            button.setCallbackData("/sendMessageToPatient " + patient.getId());
            keyboard.add(Collections.singletonList(button));
        }

        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText("❌ Отмена");
        cancelButton.setCallbackData("cancel_message");
        keyboard.add(Collections.singletonList(cancelButton));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

    public static InlineKeyboardMarkup createProfileKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> row1 = List.of(
                InlineKeyboardButton.builder()
                        .text("✏️ Редактировать профиль")
                        .callbackData("/editParameters")
                        .build(),
                InlineKeyboardButton.builder()
                        .text("📊 История здоровья")
                        .callbackData("/myHealthHistory")
                        .build()
        );


        markup.setKeyboard(List.of(row1));
        return markup;
    }
}