package com.aitherapist.aitherapist.db.dao.logic;

import com.aitherapist.aitherapist.db.entities.UserActivityLog;
import org.jvnet.hk2.annotations.Service;

import java.util.List;

@Service
public class UserActivityRegistrationService {
    public List<UserActivityLog> getByUserId(Long userId) {
        return null;
    }
}
