package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionRevisionHistoryEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface EventFunctionRevisionHistoryRepository
		extends JpaRepository<EventFunctionRevisionHistoryEntity, Long> {

	@Query("SELECT e " +
		       "FROM EventFunctionRevisionHistoryEntity e " +
		       "WHERE e.isDelete = false " +
		       "AND (:userId IS NULL OR e.userId = :userId) " +
		       "AND (:eventId IS NULL OR e.eventId = :eventId) " +
		       "ORDER BY e.revisionDate DESC")
		List<EventFunctionRevisionHistoryEntity> findAllByFilters(
		        @Param("userId") Long userId,
		        @Param("eventId") Long eventId);

	Optional<EventFunctionRevisionHistoryEntity> findByIdAndIsDeleteFalse(Long id);

}
