package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.ContactTypeMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface ContactTypeMasterRepository extends JpaRepository<ContactTypeMasterEntity, Long>{

	Optional<ContactTypeMasterEntity> findByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish,
			UserMasterEntity user);

	ContactTypeMasterEntity findByIdAndUserAndIsDeleteFalse(Long id, UserMasterEntity user);

	List<ContactTypeMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	ContactTypeMasterEntity findByIdAndIsDeleteFalse(Long id);

	boolean existsByIdAndIsDeleteFalse(Long id);

	List<ContactTypeMasterEntity> findAllByUserAndIsDeleteFalseAndIsActive(UserMasterEntity user, Boolean isActive);

	List<ContactTypeMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(String contactTypeName,
			UserMasterEntity user);

	List<ContactTypeMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndIsActive(
			String contactTypeName, UserMasterEntity user, Boolean isActive);

	@Query("SELECT c FROM ContactTypeMasterEntity c WHERE c.id = :id AND c.isDelete = FALSE")
	Optional<ContactTypeMasterEntity> getByIdAndIsDeleteFalse(Long id);
}
