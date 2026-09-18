package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventTermsAndConditionFeaturesEntity;

@Repository
public interface EventTermsAndConditionFeaturesRepository extends JpaRepository<EventTermsAndConditionFeaturesEntity, Long> {

	void deleteByEventTermsConditionId(Long id);

	List<EventTermsAndConditionFeaturesEntity> findByEventTermsConditionIdAndIsDeleteFalse(Long id);

}
