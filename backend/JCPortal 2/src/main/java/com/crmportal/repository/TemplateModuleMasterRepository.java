package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.TemplateModuleMasterEntity;

@Repository
public interface TemplateModuleMasterRepository extends JpaRepository<TemplateModuleMasterEntity, Long> {

	Optional<TemplateModuleMasterEntity> findByNameEnglishAndIsDeleteFalse(String name);
	
	Optional<TemplateModuleMasterEntity> findByIdAndIsDeleteFalse(Long id);
	
	Boolean existsByIdAndIsDeleteFalse(Long id);
	
	List<TemplateModuleMasterEntity> findAllByIsDeleteFalse();

	@Query(value = "SELECT tm  FROM TemplateModuleMasterEntity tm WHERE LOWER(tm.nameEnglish) != 'exclusive theme' AND isDelete = false")
	List<TemplateModuleMasterEntity> getTemplateModuleMasterExcluseExclusiveTheme();
}
