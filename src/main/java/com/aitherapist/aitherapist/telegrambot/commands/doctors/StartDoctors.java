package com.aitherapist.aitherapist.telegrambot.commands.doctors;

import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.interactionWithGigaApi.inputParser.ParseUserPrompt;
import com.aitherapist.aitherapist.services.DoctorServiceImpl;
import com.aitherapist.aitherapist.telegrambot.commands.ICommand;
import com.aitherapist.aitherapist.telegrambot.commands.Verification;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.DoctorRegistrationState;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.domain.enums.Answers;
import com.aitherapist.aitherapist.domain.enums.Status;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
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

@Component
@RequiredArgsConstructor
public class StartDoctors implements ICommand {
    @Autowired
    public Verification verification;
    public Doctor doctor;
    @Autowired
    public ParseUserPrompt parseUserPrompt;
    @Autowired
    private final DoctorServiceImpl doctorService;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private StringBuilder userInput = new StringBuilder();

    public SendMessage acceptOrEditDoctorInfo(User doctor, Update update) {
        String genderDisplay = doctor.getGender() ? "♂ Мужской" : "♀ Женский";

        String message = String.format("""
        📝 *Вы ввели данные:*
        
        👤 *Имя:* %s
        🎂 *Возраст:* %d лет
        🚻 *Пол:* %s
        """,
                doctor.getName(),
                doctor.getAge(),
                genderDisplay);

        return SendMessage.builder()
                .chatId(String.valueOf(TelegramIdUtils.getChatId(update)))
                .text(message + "\n\nВыберите действие:")
                .parseMode("Markdown")
                .replyMarkup(InlineKeyboardFactory.createAcceptOrEditKeyboard())
                .build();
    }

    private SendMessage handleQuestionnaire(Update update, Long userId, RegistrationContext registrationContext) throws JsonProcessingException, InterruptedException {
        Long chatId = TelegramIdUtils.getChatId(update);
        DoctorRegistrationState state = registrationContext.getDoctorRegistrationState(userId);
        if (update.getMessage().hasContact()) {
            String phoneNumber = update.getMessage().getContact().getPhoneNumber();
            registrationContext.setTelephone(userId, phoneNumber);

            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(Answers.GIVE_NAME.getMessage())
                    .build();
        }

        String text = update.getMessage().getText();
        switch (state.getCurrentStep()) {
            case 1:
                state.getUserInput().append("name: ").append(text).append("\n");
                state.setCurrentStep(2);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.AGE.getMessage())
                        .build();

            case 2:
                state.getUserInput().append("age: ").append(text).append("\n");
                state.setCurrentStep(3);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Answers.GENDER.getMessage())
                        .build();

            case 3:
                state.getUserInput().append("gender: ").append(text).append("\n");
                String response = parseUserPrompt.doctorRegistrationParser(state.getUserInput().toString());
                String jsonWithType = "{\"user_type\":\"DOCTOR\",\"role\":\"DOCTOR\"," + response.substring(1);
                try {
                    Doctor doctorInput = mapper.readValue(jsonWithType, Doctor.class);
                    doctorInput.setPhoneNumber(registrationContext.getTelephone(userId));
                    Doctor savedDoctor = doctorService.createDoctor(userId, doctorInput);
                    registrationContext.clearDoctorRegistrationState(userId);
                    return acceptOrEditDoctorInfo(savedDoctor, update);
                } catch (Exception e) {
                    return SendMessage.builder()
                            .chatId(chatId.toString())
                            .text("Произошла ошибка при сохранении данных: " + e.getMessage() + ". Попробуйте еще раз.")
                            .build();
                }

            default:
                registrationContext.clearDoctorRegistrationState(userId);
                return SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Неизвестный шаг регистрации")
                        .build();
        }
    }

    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext) {
        Long userId = TelegramIdUtils.extractUserId(update);
        if (userId == null) {
            return SendMessage.builder()
                    .chatId(TelegramIdUtils.getChatId(update).toString())
                    .text("Не удалось определить пользователя")
                    .build();
        }


        if (registrationContext.getStatus(userId) == Status.REGISTERED_DOCTOR) {
            try {
                return handleQuestionnaire(update, userId, registrationContext);
            } catch (Exception e) {
                return SendMessage.builder()
                        .chatId(TelegramIdUtils.getChatId(update).toString())
                        .text("Ошибка обработки данных")
                        .build();
            }
        } else {
            if (registrationContext.isVerify(userId)) {
                registrationContext.setStatus(userId, Status.REGISTRATION_DOCTOR);
                return requestPhoneNumber(TelegramIdUtils.getChatId(update));
            }
        }

        return SendMessage.builder()
                .chatId(TelegramIdUtils.getChatId(update).toString())
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

}
