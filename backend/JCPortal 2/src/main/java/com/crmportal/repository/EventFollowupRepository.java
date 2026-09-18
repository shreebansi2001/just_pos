package com.crmportal.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.controller.EventFollowUpEntity;

@Repository
public interface EventFollowupRepository extends JpaRepository<EventFollowUpEntity, Long> {

	@Query("SELECT e " +
	           "FROM EventFollowUpEntity e " +
	           "WHERE e.isDelete = false " +
	           "AND ( " +
	           "    (:userId IS NOT NULL AND e.userId = :userId) " +
	           "    OR " +
	           "    (:userId IS NULL AND :managerId IS NOT NULL AND e.managerId = :managerId) " +
	           "    OR " +
	           "    (:userId IS NULL AND :managerId IS NULL) " +
	           ") " +
	           "AND (:eventId IS NULL OR e.eventId = :eventId) " +
	           "AND (:isDone IS NULL OR e.isDone = :isDone) " +
	           "AND (:startDate IS NULL OR e.followUpDate >= :startDate) " +
	           "AND (:endDate IS NULL OR e.followUpDate <= :endDate) " +
	           "ORDER BY e.followUpDate ASC")
	    List<EventFollowUpEntity> findAllWithFilters(
	            @Param("userId") Long userId,
	            @Param("managerId") Long managerId,
	            @Param("eventId") Long eventId,
	            @Param("isDone") Boolean isDone,
	            @Param("startDate") LocalDate startDate,
	            @Param("endDate") LocalDate endDate);
}