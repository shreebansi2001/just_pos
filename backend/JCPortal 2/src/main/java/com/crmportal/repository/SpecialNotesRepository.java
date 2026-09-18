package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.SpecialNotesEntity;

@Repository
public interface SpecialNotesRepository extends JpaRepository<SpecialNotesEntity, Long> {

	Optional<SpecialNotesEntity> findByIdAndIsDeleteFalse(Long id);

	@Query("SELECT s FROM SpecialNotesEntity s " +
		       "WHERE s.eventFunctionId = :eventFunctionId " +
		       "AND s.userId = :userId " +
		       "AND (:managerId IS NULL OR s.managerId = :managerId OR s.managerId IS NULL) " +
		       "AND s.isDelete = false")
		List<SpecialNotesEntity> getAllSpecialNotes(
		        @Param("eventFunctionId") Long eventFunctionId,
		        @Param("managerId") Long managerId,
		        @Param("userId") Long userId);

}
