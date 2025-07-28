package com.aitherapist.aitherapist.repositories;

import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;
import com.aitherapist.aitherapist.domain.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * IUserRepository - extends from JpaRepository (extends base methods for work with database)
 * and custom methods (for extra cases)
 * JpaRepository - default java interface with methods(CRUD, ...) and add methods for Hibernate.
 * (for works with database. aka.dao)
 */
public interface IUserRepository extends JpaRepository<User, Long> {
    User findByTelegramId(Long telegramId);
}
