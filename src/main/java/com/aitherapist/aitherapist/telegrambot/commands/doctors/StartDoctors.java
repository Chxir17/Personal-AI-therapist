package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.interactionWithGigaApi.ParseUserPrompt;
import com.aitherapist.aitherapist.services.DoctorServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.Status;
import com.aitherapist.aitherapist.telegrambot.utils.createButtons.InlineKeyboardFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StartDoctors implements ICommand {
    @Autowired
    public Verification verification;
    public Doctor doctor;
    @Autowired
    private final DoctorServiceImpl doctorService;
    private int currentRegistrationStep = 1;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private StringBuilder userInput = new StringBuilder();

    private SendMessage acceptOrEditDoctorInfo(Doctor doctor, Update update) {
        Map<String, String> buttons = new HashMap<>();
        String message = "Вы ввели:\n Имя - " + doctor.getName() +
                "\n Дата рождения - " + doctor.getAge() +
                "\n Пол - " + doctor.getGender();

        buttons.put("Принять", "/acceptInitData");
        buttons.put("Изменить параметры", "/editParameters");

        return SendMessage.builder()
                .chatId(String.valueOf(getChatId(update)))
                .text(message + "\nВыберите команду")
                .replyMarkup(InlineKeyboardFactory.createInlineKeyboard(buttons, 2))
                .build();
    }

    private SendMessage handleQuestionnaire(Update update, Long userId) throws JsonProcessingException, InterruptedException {
        Long chatId = getChatId(update);

        if (!update.hasMessage()) {
            if (currentRegistrationStep == 1) {
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.GIVE_NAME.getMessage())
                        .build();
            }
            return null;
        }

        String text = update.getMessage().getText();
        System.out.println(text);
        System.out.println(currentRegistrationStep);
        switch (currentRegistrationStep) {
            case 1:
                System.out.println("--");
                userInput.append("name: ").append(text).append("\n");
                currentRegistrationStep++;
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.AGE.getMessage())
                        .build();

            case 2:
                userInput.append("age: ").append(text).append("\n");
                currentRegistrationStep++;
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.GENDER.getMessage())
                        .build();

            case 3:
                userInput.append("gender: ").append(text).append("\n");
                String response = ParseUserPrompt.doctorRegistrationParser(userInput.toString());
                String jsonWithType = "{\"user_type\":\"DOCTOR\",\"role\":\"DOCTOR\"," + response.substring(1);
                try {
                    Doctor doctorInput = mapper.readValue(jsonWithType, Doctor.class);
                    Doctor savedDoctor = doctorService.createDoctor(userId, doctorInput);
                    return acceptOrEditDoctorInfo(savedDoctor, update);
                } catch (Exception e) {
                    return SendMessage.builder()
                            .chatId(chatId.toString())
                            .text("Произошла ошибка при сохранении данных: " + e.getMessage() + ". Попробуйте еще раз.")
                            .build();
                }

            default:
                currentRegistrationStep = 0;
                userInput.setLength(0);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Неизвестный шаг регистрации")
                        .build();
        }
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) {
        Long userId = extractUserId(update);
        if (userId == null) {
            return SendMessage.builder()
                    .chatId(getChatId(update).toString())
                    .text("Не удалось определить пользователя")
                    .build();
        }

        if (registrationContext.getStatus(userId) == Status.FIRST_PART_REGISTRATION_DOCTOR) {
            try {
                return handleQuestionnaire(update, userId);
            } catch (Exception e) {
                return SendMessage.builder()
                        .chatId(getChatId(update).toString())
                        .text("Ошибка обработки данных")
                        .build();
            }
        }

        if (!registrationContext.isVerify(userId)) {
            return requestPhoneNumber(getChatId(update));
        }
        return SendMessage.builder()
                .chatId(getChatId(update).toString())
                .text("Вы уже верифицированы. Выберите действие:")
                .replyMarkup(InlineKeyboardFactory.createDoctorDefaultKeyboard())
                .build();
    }

    private SendMessage requestPhoneNumber(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(Answers.PLEASE_GIVE_TELEPHONE_NUMBER.getMessage())
                .replyMarkup(verification.createContactRequestKeyboard())
                .build();
    }

    private Long getChatId(Update update) {
        return update.hasCallbackQuery() ?
                update.getCallbackQuery().getMessage().getChatId() :
                update.getMessage().getChatId();
    }

    private Long extractUserId(Update update) {
        return update.hasMessage() ?
                update.getMessage().getFrom().getId() :
                update.hasCallbackQuery() ?
                        update.getCallbackQuery().getFrom().getId() :
                        null;
    }
}
