package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.FunctionMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface FunctionMasterRepository extends JpaRepository<FunctionMasterEntity, Long>{

	Optional<FunctionMasterEntity> findByIdAndIsDeleteFalse(long id);

	Optional<FunctionMasterEntity> findByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish, UserMasterEntity user);

	List<FunctionMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	List<FunctionMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(String functionName,
			UserMasterEntity user);

}
