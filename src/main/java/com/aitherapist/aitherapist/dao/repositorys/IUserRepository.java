package com.aitherapist.aitherapist.dao.repositorys;

import com.aitherapist.aitherapist.db.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * IUserRepository - extends from JpaRepository (extends base methods for work with database)
 * and custom methods (for extra cases)
 * JpaRepository - default java interface with methods(CRUD, ...) and add methods for Hibernate.
 * (for works with database. aka.dao)
 */
public interface IUserRepository extends JpaRepository<User, Integer> {
}
