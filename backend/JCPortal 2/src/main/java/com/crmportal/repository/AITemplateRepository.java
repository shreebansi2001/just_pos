package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.AITemplateEntity;

@Repository
public interface AITemplateRepository extends JpaRepository<AITemplateEntity, Long> {

	Optional<AITemplateEntity> findByIdAndIsDeleteFalse(Long id);

	Optional<AITemplateEntity> findByNameEnglishAndIsDeleteFalse(String nameEnglish);

	Optional<AITemplateEntity> findByNameEnglishAndIsDeleteFalseAndIdNot(String nameEnglish, Long id);

	@Query("SELECT a FROM AITemplateEntity a " + "WHERE a.isDelete = false "
			+ "AND (:isActive IS NULL OR a.isActive = :isActive)")
	List<AITemplateEntity> findAllFiltered(@Param("isActive") Boolean isActive);

	Optional<AITemplateEntity> findByIdAndIsDeleteFalseAndIsActiveTrue(Long upgradeModuleId);

}
