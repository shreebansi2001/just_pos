package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFoodTestingDetailEntity;

@Repository
public interface EventFoodTestingDetailsRepository extends JpaRepository<EventFoodTestingDetailEntity, Long> {

	void deleteByEventFoodTesting_Id(Long id);

	List<EventFoodTestingDetailEntity> findByEventFoodTestingIdAndIsDeleteFalse(Long id);
}
