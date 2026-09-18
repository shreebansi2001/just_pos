package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.ContactTypeMasterEntity;
import com.crmportal.entity.RawMaterialCategoryTypeMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface RawMaterialCategoryTypeMasterRepository extends JpaRepository<RawMaterialCategoryTypeMasterEntity, Long>{

	Optional<RawMaterialCategoryTypeMasterEntity> findByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish,
			UserMasterEntity user);

	RawMaterialCategoryTypeMasterEntity findByIdAndUserAndIsDeleteFalse(long id, UserMasterEntity user);

	List<RawMaterialCategoryTypeMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	List<RawMaterialCategoryTypeMasterEntity> findAllByUserAndIsDeleteFalseAndIsActive(UserMasterEntity user,
			Boolean isActive);

	List<RawMaterialCategoryTypeMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(
			String categoryTypeName, UserMasterEntity user);

	List<RawMaterialCategoryTypeMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndIsActive(
			String categoryTypeName, UserMasterEntity user, Boolean isActive);

	boolean existsByIdAndIsDeleteFalse(Long id);

	RawMaterialCategoryTypeMasterEntity findByIdAndIsDeleteFalse(Long id);

}
