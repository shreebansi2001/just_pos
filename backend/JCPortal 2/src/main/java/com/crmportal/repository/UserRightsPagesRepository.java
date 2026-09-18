package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.ContactCategoryMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserRightsPagesEntity;

@Repository
public interface UserRightsPagesRepository extends JpaRepository<UserRightsPagesEntity, Long>{

	List<UserRightsPagesEntity> findAllByIsDeleteFalse();

	Optional<UserMasterEntity> findByIdAndIsDeleteFalse(Long pageid);
	
	@Query(value =
	        "SELECT urp.* " +
	        "FROM userrights_pages urp " +
	        "JOIN userrights_module urm " +
	        "ON urp.module_id = urm.userrights_module_id " +
	        "WHERE urp.is_delete = false " +
	        "AND urp.is_active = true " +
	        "AND urm.is_admin_module = :isAdminModule " +
	        "AND urm.is_delete = false " +
	        "AND urm.is_active = true",
	        nativeQuery = true)
	List<UserRightsPagesEntity> findAllByIsDeleteFalseAndIsActiveTrue(
	        @Param("isAdminModule") Boolean isAdminModule);
	
	@Query(value =
	        "SELECT urp.* " +
	        "FROM userrights_pages urp " +
	        "JOIN userrights_module urm " +
	        "ON urp.module_id = urm.userrights_module_id " +
	        "WHERE urp.is_delete = false " +
	        "AND urp.is_active = true " +
	        "AND urm.is_delete = false " +
	        "AND urm.is_active = true",
	        nativeQuery = true)
	List<UserRightsPagesEntity> findAllByIsDeleteFalseAndIsActiveTrue();


}
