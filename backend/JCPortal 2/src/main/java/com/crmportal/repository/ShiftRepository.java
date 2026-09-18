package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.FunctionMasterEntity;
import com.crmportal.entity.ShiftEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface ShiftRepository extends JpaRepository<ShiftEntity, Long>{

	Optional<ShiftEntity> findByIdAndIsDeleteFalse(long id);

	Optional<ShiftEntity> findByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish, UserMasterEntity user);

	List<ShiftEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	List<ShiftEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(String shiftName, UserMasterEntity user);

}
