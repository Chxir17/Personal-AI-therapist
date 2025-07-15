package com.aitherapist.aitherapist.repositories;

import com.aitherapist.aitherapist.domain.model.entities.dailyHealthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * IHealthDataRepository - extends from JpaRepository (extends base methods for work with database)
 * and custom methods (for extra cases)
 * JpaRepository - default java interface with methods(CRUD, ...) and add methods for Hibernate.
 * (for works with database. aka.dao)
 */
@Repository
public interface IHealthDataRepository extends JpaRepository<dailyHealthData, Long>  {
}
