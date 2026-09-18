package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventGroundTaskMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.AllocationType;

@Repository
public interface EventGroundTaskMasterRepository extends JpaRepository<EventGroundTaskMasterEntity, Long> {

	List<EventGroundTaskMasterEntity> findByResourceTypeAndIsDeleteFalse(AllocationType allocationType);

	Optional<EventGroundTaskMasterEntity> findByIdAndResourceTypeAndIsDeleteFalse(Long id, AllocationType type);

	boolean existsByNameEnglishIgnoreCaseAndResourceTypeAndIsDeleteFalse(String nameEnglish,
			AllocationType resourceType);

	boolean existsByNameEnglishIgnoreCaseAndResourceTypeAndIdNotAndIsDeleteFalse(String nameEnglish,
			AllocationType resourceType, Long id);

	@Query("SELECT e FROM EventGroundTaskMasterEntity e " + "WHERE e.isDelete = false "
			+ "AND (:resourceType IS NULL OR e.resourceType = :resourceType) "
			+ "AND (:isActive IS NULL OR e.isTrue = :isActive) " + "AND (:userId IS NULL OR e.user.id = :userId) "
			+ "ORDER BY e.id DESC")
	List<EventGroundTaskMasterEntity> getAllTasks(@Param("resourceType") AllocationType resourceType,
			@Param("isActive") Boolean isActive, @Param("userId") Long userId);

	Optional<EventGroundTaskMasterEntity> findByIdAndIsDeleteFalse(Long id);

}
