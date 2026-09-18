package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.TemplateMappingEntity;
import com.crmportal.entity.TemplateMasterEntity;
import com.crmportal.entity.TemplateModuleMasterEntity;

@Repository
public interface TemplateMasterRepository extends JpaRepository<TemplateMasterEntity, Long> {

	Optional<TemplateMasterEntity> findByNameAndIsDeleteFalse(String name);

	Optional<TemplateMasterEntity> findByIdAndIsDeleteFalse(Long id);

	List<TemplateMasterEntity> findAllByIsDeleteFalse();

	Boolean existsByIdAndIsDeleteFalse(Long id);

	List<TemplateMasterEntity> findByTemplateModuleMasterAndIsDeleteFalse(
			TemplateModuleMasterEntity templateModuleMaster);
	
	List<TemplateMasterEntity> findByTemplateModuleMasterAndIsDeleteFalseAndIsActiveTrue(
			TemplateModuleMasterEntity templateModuleMaster);
	
	List<TemplateMasterEntity> findByTemplateMappingAndIsDeleteFalseAndIsActiveTrue(TemplateMappingEntity templateMappingEntity);

	List<TemplateMasterEntity> findByTemplateModuleMasterAndUserIdAndIsDeleteFalse(
			TemplateModuleMasterEntity templateModuleMasterEntity, Long userId);

	List<TemplateMasterEntity> findByIsNamePlateTrueAndIsDeleteFalse();

	List<TemplateMasterEntity> findByTemplateModuleMasterAndIsNamePlateTrueAndIsDeleteFalse(
			TemplateModuleMasterEntity templateModuleMasterEntity);

	@Query(
		    value =
		        "SELECT " +
		        " tm.template_id AS template_id, " +
		        " tm.template_name AS template_name, " +
		        " tm.created_at AS created_at, " +
		        " tm.heading_font_color AS heading_font_color, " +
		        " tm.content_font_color AS content_font_color, " +
		        " tm.front_page AS front_page, " +
		        " tm.second_front_page AS second_front_page, " +
		        " tm.watermark AS watermark, " +
		        " tm.last_main_page AS last_main_page, " +
		        " tm.is_nameplate AS is_nameplate, " +
		        " tm.nameplate_bg AS nameplate_bg, " +
		        " tm.cat_bg_page AS cat_bg_page, " +
		        " tm.extra_page AS extra_page, " +
		        " tm.dummy_pdf AS dummy_pdf, " +
		        " tm.is_active AS is_active, " +
		        " tm.is_delete AS is_delete, " +

		        " tmm.template_module_id AS module_id, " +
		        " tmm.name_english AS module_name_english, " +
		        " tmm.name_hindi AS module_name_hindi, " +
		        " tmm.name_gujarati AS module_name_gujarati, " +

		        " tmapp.id AS mapping_id, " +
		        " tmapp.name_english AS mapping_name_english, " +
		        " tmapp.name_hindi AS mapping_name_hindi, " +
		        " tmapp.name_gujarati AS mapping_name_gujarati, " +

		        " CASE WHEN atm.id IS NOT NULL THEN TRUE ELSE FALSE END AS is_selected, " +
		        
		        " tm.description_font_color " +

		        "FROM template_master tm " +
		        "INNER JOIN template_module_mst tmm " +
		        "   ON tmm.template_module_id = tm.template_module_id " +
		        "  AND tmm.is_delete = false " +
		        "INNER JOIN template_mapping tmapp " +
		        "   ON tmapp.id = tm.template_mapping_id " +
		        "  AND tmapp.is_delete = false " +
		        "LEFT JOIN admin_template_module atm " +
		        "   ON atm.template_master_id = tm.template_id " +
		        "  AND atm.user_id = :userId " +
		        "  AND atm.is_delete = false " +
		        "WHERE tm.is_delete = false " +
		        "AND ( " +
		        "       (:isNamePlate = FALSE AND tm.template_module_id = :moduleId) " +
		        "    OR (:isNamePlate = TRUE AND tm.is_nameplate = TRUE) " +
		        "    OR (:isNamePlate IS NULL AND tm.template_module_id = :moduleId AND tm.is_nameplate = TRUE) " +
		        "    )  ORDER BY tmapp.sortorder ",
		    nativeQuery = true
		)
		List<Object[]> findAllTemplatesWithSelectionNative(
		        @Param("moduleId") Long moduleId,
		        @Param("isNamePlate") Boolean isNamePlate,
		        @Param("userId") Long userId
		);


}
