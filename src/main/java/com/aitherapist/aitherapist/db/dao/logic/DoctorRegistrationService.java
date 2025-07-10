package com.aitherapist.aitherapist.db.dao.logic;

import com.aitherapist.aitherapist.db.entities.UserActivityLog;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Service
public class DoctorRegistrationService {

    public List<UserActivityLog> getByUserId(Long userId) {}
}
