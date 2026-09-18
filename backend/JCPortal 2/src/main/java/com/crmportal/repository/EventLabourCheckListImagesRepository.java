package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventLabourChecklistPhotosEntity;

@Repository
public interface EventLabourCheckListImagesRepository extends JpaRepository<EventLabourChecklistPhotosEntity, Long> {

	EventLabourChecklistPhotosEntity findByIdAndIsDeleteFalse(Long moduleRecordId);

	@Query(value = "SELECT * FROM checklist_images " + "WHERE is_delete = false "
			+ "AND eventlabour_checklist_id IN (:ids)", nativeQuery = true)
	List<EventLabourChecklistPhotosEntity> findImages(@Param("ids") List<Long> ids);

}
