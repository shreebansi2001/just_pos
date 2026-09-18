package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.UserAssigneAITemplateEntity;

@Repository
public interface UserAssigneAiTemplateRepository extends JpaRepository<UserAssigneAITemplateEntity, Long> {

	UserAssigneAITemplateEntity findByUserIdAndIsDeleteFalseAndIsActiveTrueAndAiTemplateId(Long userId, Long moduleId);

	List<UserAssigneAITemplateEntity> findByUserIdAndIsDeleteFalseAndIsActiveTrueAndAiTemplateIdIn(Long userId,
			List<Long> moduleId);

	List<UserAssigneAITemplateEntity> findByUserIdAndIsDeleteFalseAndStartDateIsNullAndEndDateIsNullAndAiTemplateIdIn(
			Long userId, List<Long> moduleIds);

	List<UserAssigneAITemplateEntity> findByAiTemplateIdAndIsActiveTrue(Long id);

}
