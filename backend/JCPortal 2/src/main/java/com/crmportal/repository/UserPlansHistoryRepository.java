package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PlansEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserPlansHistoryEntity;

@Repository
public interface UserPlansHistoryRepository extends JpaRepository<UserPlansHistoryEntity, Long>{

	Optional<UserPlansHistoryEntity>  findByUserAndIsActiveTrue(UserMasterEntity user);
	
	List<UserPlansHistoryEntity> findByIsActive(boolean isActive);
	
	UserPlansHistoryEntity findByUserAndPlanAndIsActiveTrue(UserMasterEntity user, PlansEntity plan);

	@Query("SELECT up FROM UserPlansHistoryEntity up WHERE up.user = :user ORDER BY up.createdAt DESC")
	List<UserPlansHistoryEntity> findAllByUserOrderByCreatedAtDesc(@Param("user") UserMasterEntity user);

}
