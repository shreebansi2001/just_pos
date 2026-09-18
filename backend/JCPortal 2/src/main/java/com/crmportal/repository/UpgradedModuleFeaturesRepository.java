package com.crmportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UpgradedModuleFeaturesEntity;

@Repository
public interface UpgradedModuleFeaturesRepository extends JpaRepository<UpgradedModuleFeaturesEntity, Long>{

	Optional<UpgradedModuleFeaturesEntity> findByIdAndIsDeleteFalse(Long id);

	void deleteAllByUpgradedModuleId(Long id);

}
