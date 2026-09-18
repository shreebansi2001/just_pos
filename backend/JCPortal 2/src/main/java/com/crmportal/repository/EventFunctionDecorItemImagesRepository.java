package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crmportal.entity.EventFunctionDecorItemImagesEntity;

public interface EventFunctionDecorItemImagesRepository
		extends JpaRepository<EventFunctionDecorItemImagesEntity, Long> {

	Optional<EventFunctionDecorItemImagesEntity> findByIdAndIsDeleteFalse(Long moduleRecordId);

	void deleteByEventIdAndEventFunctionIdAndDecorItemId(Long eventId, Long eventFunctionId, Long decorItemId);

	List<EventFunctionDecorItemImagesEntity> findByEventIdAndEventFunctionIdAndDecorItemIdAndIsDeleteFalse(Long eventId,
			Long eventFunctionId, Long decorItemId);
}
