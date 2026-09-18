package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UserAmcEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.response.dto.UserAmcResponseDto;

@Repository
public interface UserAmcRepository extends JpaRepository<UserAmcEntity, Long> {

	List<UserAmcEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	UserAmcEntity findByIdAndUser(Long id, UserMasterEntity user);

	UserAmcEntity findByIdAndUserAndIsDeleteFalse(Long id, UserMasterEntity user);

	UserAmcEntity findByIdAndIsDeleteFalse(Long id);

	List<UserAmcEntity> findByIsDeleteFalse();

	boolean existsByIdAndIsDeleteFalse(Long id);
}
