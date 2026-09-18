package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.RefundDetailsEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface RefundDetailsRepository extends JpaRepository<RefundDetailsEntity, Long> {

	List<RefundDetailsEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	RefundDetailsEntity findByIdAndUser(Long id, UserMasterEntity user);

	RefundDetailsEntity findByIdAndUserAndIsDeleteFalse(Long id, UserMasterEntity user);

	RefundDetailsEntity findByIdAndIsDeleteFalse(Long id);

	boolean existsByIdAndIsDeleteFalse(Long id);

}
