package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.TesterMasterEntity;

@Repository
public interface TesterMasterRepository extends JpaRepository<TesterMasterEntity, Long> {

	Optional<TesterMasterEntity> findByContactNoAndUserIdAndIsDeleteFalse(String contactNo, Long userId);
	
	Optional<TesterMasterEntity> findByEmailAndUserIdAndIsDeleteFalse(String email, Long userId);
	
	Optional<TesterMasterEntity> findByIdAndIsDeleteFalse(Long id);
	
	List<TesterMasterEntity> findAllByUserIdAndIsDeleteFalse(Long userId);

	Optional<TesterMasterEntity> findByContactNoAndIsDeleteFalse(String contactNo);
}
