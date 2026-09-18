package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UserDocumentInfoEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface UserDocumentInfoRepository extends JpaRepository<UserDocumentInfoEntity, Long>{

	UserDocumentInfoEntity findByIdAndIsDeleteFalseAndUser(Long id, UserMasterEntity user);

	UserDocumentInfoEntity findByIdAndIsDeleteFalse(Long moduleId);

	List<UserDocumentInfoEntity> findAllByUser(UserMasterEntity user);

	boolean existsByIdAndIsDeleteFalse(Long id);

	List<UserDocumentInfoEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);


}
