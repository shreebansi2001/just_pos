package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UserBasicFileEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface UserBasicFileRepository extends JpaRepository<UserBasicFileEntity, Long>{

	Optional<UserBasicFileEntity> findByIdAndIsDeleteFalse(Long moduleRecordId);

	List<UserBasicFileEntity> findAllByUserIdAndIsDeleteFalse(Long id);

	Optional<UserBasicFileEntity> findByIdAndIsDeleteFalseAndModuleType(Long id, String fileType);

	boolean existsByIdAndIsDeleteFalseAndModuleType(Long id,String fileType);

}
