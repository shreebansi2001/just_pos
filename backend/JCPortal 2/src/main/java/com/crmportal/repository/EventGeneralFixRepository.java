package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventGeneralFixEntity;

@Repository
public interface EventGeneralFixRepository extends JpaRepository<EventGeneralFixEntity, Long> {

	boolean existsByEventId(Long eventId);

	List<EventGeneralFixEntity> findByEventId(Long eventId);

	List<EventGeneralFixEntity> findByEventIdAndRawCatIdIn(Long eventId, List<Long> rawCatIds);

}
