package com.crmportal.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UserUpgradedModuleEntity;

@Repository
public interface UserUpgradedModuleRepository extends JpaRepository<UserUpgradedModuleEntity, Long>{

	UserUpgradedModuleEntity findByUserIdAndIsDeleteFalseAndIsActiveTrue(Long id);

	@Query(value = "SELECT " +
            "uum.user_upgrade_module_id, " +
            "uum.user_id, " +
            "uum.upgrade_module_id, " +
            "uum.end_date, " +
            "u.email, " +
            "u.first_name, " +
            "u.last_name, " +
            "um.module_name " +
            "FROM user_upgraded_module uum " +
            "JOIN users u ON uum.user_id = u.user_id " +
            "JOIN upgrade_module um ON uum.upgrade_module_id = um.upgrade_module_id " +
            "WHERE uum.end_date BETWEEN :start AND :end " +
            "AND uum.is_active = true " +
            "AND uum.is_delete = false",
            nativeQuery = true)
    List<Object[]> findExpiringModules(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    @Query(value = "SELECT " +
            "uum.user_upgrade_module_id, " +
            "uum.user_id, " +
            "uum.upgrade_module_id, " +
            "uum.end_date, " +
            "u.email, " +
            "u.first_name, " +
            "u.last_name, " +
            "um.module_name " +
            "FROM user_upgraded_module uum " +
            "JOIN users u ON uum.user_id = u.user_id " +
            "JOIN upgrade_module um ON uum.upgrade_module_id = um.upgrade_module_id " +
            "WHERE uum.end_date < :now " +
            "AND uum.is_active = true " +
            "AND uum.is_delete = false",
            nativeQuery = true)
    List<Object[]> findExpiredModules(@Param("now") LocalDateTime now);

    @Query(value = "SELECT " +
            "uum.user_upgrade_module_id, " +
            "uum.user_id, " +
            "uum.upgrade_module_id, " +
            "um.module_name, " +
            "uum.start_date, " +
            "uum.end_date, " +
            "uum.is_active, " +
            "uum.is_delete, " +
            "uum.is_pay_done, " +
            "uum.pay_amount, " +
            "um.billing_cycle " +
            "FROM user_upgraded_module uum " +
            "JOIN users u ON uum.user_id = u.user_id " +
            "JOIN upgrade_module um ON uum.upgrade_module_id = um.upgrade_module_id " +
            "WHERE uum.user_id = :userId " + 
            "AND uum.is_active = true " +
            "AND uum.is_delete = false " +
            "ORDER BY uum.user_upgrade_module_id DESC",
            nativeQuery = true)
    List<Object[]> getActiveUpgradedModuleByUser(Long userId);

	UserUpgradedModuleEntity findByUserIdAndIsDeleteFalseAndStartDateIsNullAndEndDateIsNull(Long userId);

	List<UserUpgradedModuleEntity> findByUserIdAndIsDeleteFalseAndStartDateIsNullAndEndDateIsNullAndUpgradeModuleIdIn(
			Long userId, List<Long> moduleId);

	UserUpgradedModuleEntity findByUserIdAndIsDeleteFalseAndIsActiveTrueAndUpgradeModuleId(Long id, Long id2);

	List<UserUpgradedModuleEntity> findByUpgradeModuleIdAndIsActiveTrue(Long id); 

	List<UserUpgradedModuleEntity> findByUserIdAndIsDeleteFalseAndIsActiveTrueAndUpgradeModuleIdIn(Long userId,
			List<Long> moduleId);

	boolean existsByUserIdAndUpgradeModuleIdAndIsActiveTrueAndIsDeleteFalse(Long id, Long i);

}
