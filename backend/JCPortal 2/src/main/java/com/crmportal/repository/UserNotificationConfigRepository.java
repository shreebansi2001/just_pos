package com.crmportal.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserNotificationConfigEntity;

@Repository
public interface UserNotificationConfigRepository extends JpaRepository<UserNotificationConfigEntity, Long> {

	UserNotificationConfigEntity findByUserIdAndIsDeleteFalseAndIsActiveTrueAndUpgradeModuleId(Long id, Long id2);

	List<UserNotificationConfigEntity> findByUserIdAndIsDeleteFalseAndStartDateIsNullAndEndDateIsNullAndUpgradeModuleIdIn(
			Long userId, List<Long> moduleIds);

	Optional<UserNotificationConfigEntity> findByIdAndIsDeleteFalseAndIsActiveTrueAndIsPayDoneTrue(Long id);

	@Query(value = "SELECT " +
	        "unc.unc_id, unc.key1, unc.key2, unc.url, unc.is_active, unc.is_delete, unc.is_pay_done, " +
	        "um.upgrade_module_id, um.module_name, " +
	        "unc.start_date, unc.end_date, " +
	        "unc.user_id, unc.pay_amount " +
	        "FROM user_notification_config unc " +
	        "JOIN upgrade_module um ON unc.upgrade_module_id = um.upgrade_module_id " +
	        "WHERE unc.user_id = :userId " +
	        "AND unc.is_active = true " +
	        "AND unc.is_pay_done = true " +
	        "AND unc.is_delete = false " +
	        "AND um.is_delete = false",
	        nativeQuery = true)
	List<Object[]> findAllValidConfigsByUser(@Param("userId") Long userId);

	@Query(value = "SELECT " +
            "unc.unc_id, " +
            "unc.user_id, " +
            "unc.upgrade_module_id, " +
            "unc.end_date, " +
            "u.email, " +
            "u.first_name, " +
            "u.last_name, " +
            "um.module_name " +
            "FROM user_notification_config unc " +
            "JOIN users u ON unc.user_id = u.user_id " +
            "JOIN upgrade_module um ON unc.upgrade_module_id = um.upgrade_module_id " +
            "WHERE unc.end_date BETWEEN :start AND :end " +
            "AND unc.is_active = true " +
            "AND unc.is_delete = false",
            nativeQuery = true)
	List<Object[]> findExpiringModules(LocalDateTime start, LocalDateTime end);

	@Query(value = "SELECT " +
            "unc.unc_id, " +
            "unc.user_id, " +
            "unc.upgrade_module_id, " +
            "unc.end_date, " +
            "u.email, " +
            "u.first_name, " +
            "u.last_name, " +
            "um.module_name " +
            "FROM user_notification_config unc " +
            "JOIN users u ON unc.user_id = u.user_id " +
            "JOIN upgrade_module um ON unc.upgrade_module_id = um.upgrade_module_id " +
            "WHERE unc.end_date < :now " +
            "AND unc.is_active = true " +
            "AND unc.is_delete = false",
            nativeQuery = true)
	List<Object[]> findExpiredModules(LocalDateTime now);

	@Query(value = "SELECT " +
	        "unc.* " +
	        "FROM user_notification_config unc " +
	        "JOIN upgrade_module um ON unc.upgrade_module_id = um.upgrade_module_id " +
	        "WHERE unc.user_id = :userId " +
	        "AND unc.is_active = true " +
	        "AND unc.is_pay_done = true " +
	        "AND unc.is_delete = false " +
	        "AND um.is_delete = false " +
	        "AND unc.upgrade_module_id = :upgradeModuleId",
	        nativeQuery = true)
	UserNotificationConfigEntity findNotificationByUserAndUpgradeModule(Long userId,Long upgradeModuleId);
	
	@Query(value = "SELECT " +
	        "unc.unc_id, unc.key1, unc.key2, unc.url, unc.is_active, unc.is_delete, unc.is_pay_done, " +
	        "um.upgrade_module_id, um.module_name, " +
	        "unc.start_date, unc.end_date, " +
	        "unc.user_id, unc.pay_amount " +
	        "FROM user_notification_config unc " +
	        "JOIN upgrade_module um ON unc.upgrade_module_id = um.upgrade_module_id " +
	        "WHERE unc.user_id = :userId " +
	        "AND unc.is_active = true " +
	        "AND unc.is_pay_done = true " +
	        "AND unc.is_delete = false " +
	        "AND um.is_delete = false " +
	        "AND unc.upgrade_module_id = :upgradeModuleId",
	        nativeQuery = true)
	List<Object[]> findlValidConfigByUserAndUpgradeModule(Long userId,Long upgradeModuleId);

}
