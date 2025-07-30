package com.aitherapist.aitherapist.telegrambot.commands;

import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.Doctor;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.domain.model.entities.User;
import com.aitherapist.aitherapist.repositories.*;
import com.aitherapist.aitherapist.services.interfaces.INotificationService;
import com.aitherapist.aitherapist.services.interfaces.IUserService;
import com.aitherapist.aitherapist.telegrambot.ITelegramExecutor;
import com.aitherapist.aitherapist.telegrambot.messageshandler.contexts.RegistrationContext;
import com.aitherapist.aitherapist.telegrambot.utils.TelegramIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;

@Component
public class DeleteData implements ICommand {

    private final IUserRepository userRepository;
    private final IDailyHealthDataRepository dailyHealthDataRepository;
    private final IInitialHealthDataRepository initialHealthDataRepository;
    private final IUserActivityLogRepository userActivityLogRepository;
    private final IDoctorRepository doctorRepository;
    private final INotificationRepository notificationRepository;

    @Autowired
    public DeleteData(IUserRepository userRepository,
                      IDailyHealthDataRepository dailyHealthDataRepository,
                      IInitialHealthDataRepository initialHealthDataRepository,
                      IUserActivityLogRepository userActivityLogRepository,
                      IDoctorRepository doctorRepository,
                      INotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.dailyHealthDataRepository = dailyHealthDataRepository;
        this.initialHealthDataRepository = initialHealthDataRepository;
        this.userActivityLogRepository = userActivityLogRepository;
        this.doctorRepository = doctorRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    @Override
    public SendMessage apply(Update update, RegistrationContext registrationContext, ITelegramExecutor telegramExecutor) throws TelegramApiException {
        Long userId = TelegramIdUtils.getChatId(update);
        registrationContext.deleteAllDataOfUser(userId);
        User user = userRepository.findByTelegramId(userId);

        if (user == null) {
            return new SendMessage(userId.toString(), "Пользователь не найден.");
        }

        // 1. Удаляем DailyHealthData (добавь метод в репозиторий!)
        if (user instanceof Patient patient) {
            dailyHealthDataRepository.deleteAllByPatientId(patient.getId());
        }

        // 2. Удаляем InitialHealthData
        if (user instanceof Patient patient && patient.getInitialData() != null) {
            initialHealthDataRepository.deleteById(patient.getInitialData().getId());
        }

        // 3. Очищаем doctor-patient связи
        if (user instanceof ClinicPatient clinicPatient) {
            for (Doctor doctor : new ArrayList<>(clinicPatient.getDoctors())) {
                doctor.removePatient(clinicPatient);
                doctorRepository.save(doctor);
            }
        }

        // 4. Удаляем NotificationConfig
        if (user.getNotificationConfig() != null) {
            notificationRepository.deleteById(user.getNotificationConfig().getId());
        }

        // 5. Удаляем UserActivityLogs (добавь метод в репозиторий!)
        userActivityLogRepository.deleteAllByUserId(user.getId());

        // 6. Удаляем ScheduledNotifications (если есть связь)
        // Добавь репозиторий IScheduledNotificationRepository и метод deleteAllByInternalUserId
        // scheduledNotificationRepository.deleteAllByInternalUserId(user.getId());

        // 7. Удаляем самого пользователя
        userRepository.deleteById(user.getId());

        return new SendMessage(userId.toString(), "Все ваши данные были удалены.");
    }
}
