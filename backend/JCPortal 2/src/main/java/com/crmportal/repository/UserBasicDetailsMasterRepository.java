package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.RoleMasterEntity;
import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.request.dto.UserMasterRequestDto;

@Repository
public interface UserBasicDetailsMasterRepository extends JpaRepository<UserBasicDetailsMasterEntity, Long> {

	Optional<UserBasicDetailsMasterEntity> findByUserAndIsDeleteFalse(UserMasterEntity entity);

	UserBasicDetailsMasterEntity findByUser(UserMasterEntity userMasterEntity);

	List<UserBasicDetailsMasterEntity> findAllByRoleAndIsDeleteFalse(RoleMasterEntity role);

	List<UserBasicDetailsMasterEntity> findAllByTypeIgnoreCaseAndIsDeleteFalse(String type);

	List<UserBasicDetailsMasterEntity> findAllByRoleAndTypeIgnoreCaseAndIsDeleteFalse(RoleMasterEntity role,
			String type);

	Long countByRoleAndIsDeleteFalse(RoleMasterEntity role);

	@Query("SELECT COUNT(ubd) FROM UserBasicDetailsMasterEntity ubd " + "JOIN ubd.user u " + "JOIN ubd.role r "
			+ "WHERE ubd.isDelete = false " + "AND u.isActive = true " + "AND r = :role")
	Long countByRoleAndIsDeleteFalseAndIsActiveTrue(@Param("role") RoleMasterEntity role);

	Long countByRoleAndTypeIgnoreCaseAndIsDeleteFalse(@Param("role") RoleMasterEntity role, @Param("type") String type);

	@Query(value = "SELECT COUNT(*) " + "FROM user_basic_details ubd " + "LEFT JOIN users u ON u.user_id = ubd.user_id "
			+ "WHERE ubd.is_delete = FALSE " + "AND u.is_delete = FALSE "
			+ "AND (:flag = FALSE OR u.is_active = :isActive) " + "AND ubd.role_id = :roleId "
			+ "AND (UPPER(:type) = 'ALL' OR ubd.type = :type) ", nativeQuery = true)
	Long getActiveUserByRoleAndType(@Param("roleId") Long roleId, @Param("type") String type,
			@Param("flag") Boolean flag, @Param("isActive") Boolean isActive);

	@Query("SELECT COUNT(u) " + "FROM UserMasterEntity u " + "WHERE u.isDelete = false "
			+ "AND u.userBasicDetails.type = 'member' " + "AND u.userBasicDetails.role.id = 2 "
			+ "AND u.isDelete = false AND u.userBasicDetails.isDelete = false " + "AND u.id NOT IN ( "
			+ "   SELECT DISTINCT i.customerId " + "   FROM InvoiceEntity i " + "   WHERE i.isDelete = false " + ")")
	Long countUsersWithoutInvoice();

	@Query("SELECT ubd.lang FROM UserBasicDetailsMasterEntity ubd WHERE ubd.user.id = :userId ")
	String getLangByUserId(Long userId);

	UserBasicDetailsMasterEntity findByUser_IdAndIsDeleteFalse(Long clientId);

	List<UserBasicDetailsMasterEntity> findByUser_IdIn(List<Long> userIds);
}
