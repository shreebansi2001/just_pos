package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionManagerTaskImagesEntity;
import com.crmportal.entity.EventLabourChecklistEntity;

@Repository
public interface EventFunctionManagerTaskImagesRepository
		extends JpaRepository<EventFunctionManagerTaskImagesEntity, Long> {

	Optional<EventFunctionManagerTaskImagesEntity> findByIdAndIsDeleteFalse(Long moduleRecordId);

	List<EventFunctionManagerTaskImagesEntity> findByEventFunctionManagerTaskIdAndIsDeleteFalse(Long id);

}
