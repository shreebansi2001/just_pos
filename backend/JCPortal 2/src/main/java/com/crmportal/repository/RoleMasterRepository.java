package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.RoleMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface RoleMasterRepository extends JpaRepository<RoleMasterEntity, Long>{

	Optional<RoleMasterEntity> findByIdAndIsDeleteFalse(long id);

	Optional<RoleMasterEntity> findByNameAndIsDeleteFalse(String name);

	List<RoleMasterEntity> findAllByIsDeleteFalse();

	boolean existsByIdAndIsDeleteFalse(Long id);

	Optional<RoleMasterEntity> findByNameAndUserAndIsDeleteFalse(String name, UserMasterEntity user);

	List<RoleMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	List<RoleMasterEntity> findByNameContainingIgnoreCaseAndUserAndIsDeleteFalse(String roleName,
			UserMasterEntity user);

}
