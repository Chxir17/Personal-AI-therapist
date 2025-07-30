package com.aitherapist.aitherapist.repositories;

import com.aitherapist.aitherapist.domain.model.entities.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface IUserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM UserActivityLog u WHERE u.user.id = :userId")
    void deleteAllByUserId(Long userId);
}
