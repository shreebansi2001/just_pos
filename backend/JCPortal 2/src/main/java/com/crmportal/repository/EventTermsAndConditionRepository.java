package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventTermsAndConditionEntity;

@Repository
public interface EventTermsAndConditionRepository extends JpaRepository<EventTermsAndConditionEntity, Long> {

	List<EventTermsAndConditionEntity> findByEventIdAndIsDeleteFalse(Long eventId);
	
	Optional<EventTermsAndConditionEntity> findByEventIdAndNameEnglishAndIsDeleteFalse(Long eventId, String nameEnglish);

}
