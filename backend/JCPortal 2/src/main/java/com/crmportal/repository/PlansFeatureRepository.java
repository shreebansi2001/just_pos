package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PlanFeatureEntity;
import com.crmportal.entity.PlansEntity;

@Repository
public interface PlansFeatureRepository extends JpaRepository<PlanFeatureEntity, Long>{
	List<PlanFeatureEntity> findAllByPlanAndIsDeleteFalse(PlansEntity entity);

	PlanFeatureEntity findByPlanAndIsDeleteFalse(PlansEntity entity);

}
