package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.ResponseBody;

import com.crmportal.entity.TemplateMappingEntity;
import com.crmportal.entity.TemplateModuleMasterEntity;

@ResponseBody
public interface TemplateMappingRepository extends JpaRepository<TemplateMappingEntity, Long> {

	TemplateMappingEntity findByIdAndIsDeleteFalse(Long id);

	List<TemplateMappingEntity> findByIsDeleteFalse();

	List<TemplateMappingEntity> findByTemplateModuleIdAndIsDeleteFalse(Long templateModuleId);
	
	List<TemplateMappingEntity> findByTemplateModuleAndIsDeleteFalse(TemplateModuleMasterEntity templateModule);
	
	@Query("SELECT t FROM TemplateMappingEntity t " +
		       "JOIN t.templateModule tm " +
		       "WHERE tm.id = :moduleId " +
		       "AND t.isDelete = false " +
		       "AND NOT ( " +
		       "   (" +
		       " 	UPPER(tm.nameEnglish) = 'NAME PLATE THEME' " +
		       " 		AND (t.nameEnglish IN " +
		       " 			('Type 6', 'Type 5', 'Type 8', 'Type 9', 'Type 10', 'Type 11', " +
		       " 			 'Type 12', 'Type 13', 'Type 14', 'Type 15', 'Type 16', 'Type 17'))) " +
		       "    OR " +
		       "    (UPPER(tm.nameEnglish) = 'INVOICE REPORTS' AND t.nameEnglish IN "
		       + " 			('Type 2', 'Type 3', 'Type 4', 'Type 5','Type 6','Type 7', 'Type 8', 'Type 9', 'Type 10')) " +
		       "    OR " +
		       "    (UPPER(tm.nameEnglish) = 'QUOTATION REPORTS' AND t.nameEnglish IN "
		       + " 			('Type 2', 'Type 3', 'Type 4', 'Type 5', 'Type 6','Type 7', 'Type 8', 'Type 9', 'Type 10'))" +
		       "    OR " +
		       "    (UPPER(tm.nameEnglish) = 'MENU ALLOCATION THEME' AND t.nameEnglish IN ('Type 3','Type 7', 'Type 5')) " +
		       "    OR " +
		       "    (UPPER(tm.nameEnglish) = 'BACK OFFICE THEME' AND (t.nameEnglish IN ('Type 8', 'Type 9', 'Type 10', 'Type 11', 'Type 12','Type 13', 'Type 14', 'Type 15'))) " +
		       "    OR " +
		       "    (UPPER(tm.nameEnglish) = 'LEAD MODULE') " +
		       "    OR " +
		       "    (UPPER(tm.nameEnglish) = 'ORDER SUMMARY THEME' AND (t.nameEnglish IN ('Type 2'))) " +
		       " 	OR " +
		       " 	(UPPER(tm.nameEnglish) = 'Work Report Theme') " +
		       ")")
	List<TemplateMappingEntity> findTemplates(@Param("moduleId") Long moduleId);
	
	Optional<TemplateMappingEntity> findByNameEnglishAndIsDeleteFalse(String nameEnglish);

	Optional<TemplateMappingEntity> findByNameEnglishAndIsDeleteFalseAndTemplateModule(String nameEnglish,
			TemplateModuleMasterEntity moduleMasterEntity);

}
