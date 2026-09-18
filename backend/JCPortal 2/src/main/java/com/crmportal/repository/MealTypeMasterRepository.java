package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.MealTypeMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface MealTypeMasterRepository extends JpaRepository<MealTypeMasterEntity, Long>{

	Optional<MealTypeMasterEntity> findByNameEnglishAndUserAndIsDeleteFalse(String nameEnglish, UserMasterEntity user);

	Optional<MealTypeMasterEntity> findByIdAndIsDeleteFalse(long id);

	List<MealTypeMasterEntity> findAllByUserAndIsDeleteFalse(UserMasterEntity user);

	List<MealTypeMasterEntity> findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(String mealTypeName,
			UserMasterEntity user);

}
