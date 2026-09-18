package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.RoleHierarchyEntity;
import com.crmportal.entity.RoleMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface RoleHierarchyRepository extends JpaRepository<RoleHierarchyEntity, Long> {


	List<RoleHierarchyEntity> findByParentRoleAndIsDeleteFalseAndUser(RoleMasterEntity currentRole,
			UserMasterEntity user);

	boolean existsByParentRoleAndChildRoleAndUserAndIsDeleteFalse(RoleMasterEntity parentRole,
			RoleMasterEntity childRole, UserMasterEntity user);

	boolean existsByParentRoleAndChildRoleAndUserAndIsDeleteFalseAndIdNot(RoleMasterEntity parentRole,
			RoleMasterEntity childRole, UserMasterEntity user, Long hierarchyId);

	List<RoleHierarchyEntity> findByUserAndIsDeleteFalse(UserMasterEntity user);

	List<RoleHierarchyEntity> findByChildRoleAndUserAndIsDeleteFalse(RoleMasterEntity role, UserMasterEntity user);

}
