package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PlansEntity;

@Repository
public interface PlansRepository extends JpaRepository<PlansEntity, Long>{

	Optional<PlansEntity> findByNameAndIsDeleteFalse(String name);

	Optional<PlansEntity> findByIdAndIsDeleteFalse(long id);

	List<PlansEntity> findAllByIsDeleteFalse();
	
	@Query(value = "SELECT pe FROM PlansEntity pe WHERE pe.isDelete = FALSE AND pe.id != 3")
	List<PlansEntity> findAllByIsDeleteFalseAndExcludeUnlimitedPlan();

	boolean existsByIdAndIsDeleteFalse(Long id);

	List<PlansEntity> findAllByBillingCycleAndIsDeleteFalse(String billingCycle);

	Optional<PlansEntity> findByNameAndBillingCycleAndIsDeleteFalse(String name, String billingCycle);

	Optional<PlansEntity> findByNameAndBillingCycleAndIdNotAndIsDeleteFalse(String name, String billingCycle, long id);

}
