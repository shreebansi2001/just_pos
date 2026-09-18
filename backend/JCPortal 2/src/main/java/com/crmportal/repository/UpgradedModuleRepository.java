package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UpgradedModuleEntity;

@Repository
public interface UpgradedModuleRepository extends JpaRepository<UpgradedModuleEntity, Long> {

	Optional<UpgradedModuleEntity> findByIdAndIsDeleteFalse(Long id);

	Optional<UpgradedModuleEntity> findByIdAndIsDeleteFalseAndIsActiveTrue(Long upgradeModuleId);

	@Query(value = "SELECT "
	        + "um.upgrade_module_id AS module_id, "
	        + "um.module_name AS module_name, "
	        + "um.price AS module_price, "
	        + "um.description AS module_description, "
	        + "um.is_active AS module_is_active, "
	        + "um.created_at AS module_created_at, "
	        + "um.billing_cycle AS billing_cycle, "
	        + "um.is_config AS module_is_config, "

	        + "uf.upgradedmodule_features_id AS feature_id, "
	        + "uf.feature_text AS feature_text, "
	        + "uf.created_at AS feature_created_at, "
	        + "uf.upgraded_module_id AS feature_module_id, "

	        + "CASE "
	        + " WHEN (um.is_config = TRUE AND unc.unc_id IS NOT NULL) "
	        + "   OR (um.is_config = FALSE AND uum.user_upgrade_module_id IS NOT NULL) "
	        + " THEN TRUE ELSE FALSE END AS is_purchased, "
	        
	        +" unc.unc_id as uncId, "
	        +" unc.key1 as key1,"
	        +" unc.key2 as key2,"
	        +" unc.url as url,"
	        +" unc.start_date,"
	        +" unc.end_date"
	        
	        +" FROM upgrade_module um "

	        + "LEFT JOIN upgradedmodule_features uf "
	        + " ON um.upgrade_module_id = uf.upgraded_module_id "
	        + " AND uf.is_delete = FALSE "

	        + "LEFT JOIN user_notification_config unc "
	        + " ON um.upgrade_module_id = unc.upgrade_module_id "
	        + " AND unc.user_id = :userId "
	        + " AND unc.is_delete = FALSE "
	        + " AND unc.is_active = TRUE "
	        + " AND unc.is_pay_done = TRUE "

	        + "LEFT JOIN user_upgraded_module uum "
	        + " ON um.upgrade_module_id = uum.upgrade_module_id "
	        + " AND uum.user_id = :userId "
	        + " AND uum.is_delete = FALSE "
	        + " AND uum.is_active = TRUE "
	        + " AND uum.is_pay_done = TRUE "

	        + "WHERE um.is_delete = FALSE "
	        + "AND (:isActive IS NULL OR um.is_active = :isActive) "
	        + "AND (:isConfig IS NULL OR um.is_config = :isConfig) "

	        + "ORDER BY um.upgrade_module_id",
	        nativeQuery = true)
	List<Object[]> getAllModules(Boolean isActive, Long userId, Boolean isConfig);

	boolean existsByModuleNameAndIsDeleteFalse(String moduleName);

	boolean existsByModuleNameAndIdNotAndIsDeleteFalse(String moduleName, Long id);

}
