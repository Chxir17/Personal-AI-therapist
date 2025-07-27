package com.aitherapist.aitherapist.repositories;

import com.aitherapist.aitherapist.domain.model.entities.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
}
