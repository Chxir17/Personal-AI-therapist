package com.aitherapist.aitherapist.db.dao.repositorys;

import com.aitherapist.aitherapist.db.entities.HealthData;
import com.aitherapist.aitherapist.db.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * IHealthDataRepository - extends from JpaRepository (extends base methods for work with database)
 * and custom methods (for extra cases)
 * JpaRepository - default java interface with methods(CRUD, ...) and add methods for Hibernate.
 * (for works with database. aka.dao)
 */
@Repository
public interface IHealthDataRepository extends JpaRepository<HealthData, Integer>  {
    List<HealthData> findByUser(User user);  // Spring Data JPA auto implement methods.
}
