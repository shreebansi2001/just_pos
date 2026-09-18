package com.crmportal.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.AdminTemplateModuleEntity;
import com.crmportal.entity.TemplateMappingEntity;
import com.crmportal.entity.TemplateMasterEntity;
import com.crmportal.entity.TemplateModuleMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface AdminTemplateModuleRepository extends JpaRepository<AdminTemplateModuleEntity, Long> {

	Optional<AdminTemplateModuleEntity> findByIdAndIsDeleteFalse(Long id);

	List<AdminTemplateModuleEntity> findAllByIsDeleteFalse();

	List<AdminTemplateModuleEntity> findAllByUserIdAndIsDeleteFalse(Long userId);

	Optional<AdminTemplateModuleEntity> findByIdAndUserIdAndIsDeleteFalse(Long id, Long userId);

	List<AdminTemplateModuleEntity> findAllByUserIdAndIsDeleteFalseAndTemplateModuleMaster_id(Long userId,
			Long templateModuleId);

	@Query(value = "SELECT " + "tmm.template_module_id, " + "tmm.name_english, " + "tm.template_id, "
			+ "tm.template_name, atm.id " + "FROM admin_template_module atm " + "JOIN template_master tm "
			+ "ON atm.template_master_id = tm.template_id " + "JOIN template_module_mst tmm "
			+ "ON atm.template_module_master_id = tmm.template_module_id "
			+ "WHERE atm.user_id = :userId AND atm.is_delete = false", nativeQuery = true)
	List<Object[]> getAllUserTemplates(@Param("userId") Long userId);

	List<AdminTemplateModuleEntity> findByTemplateMasterAndUserIdAndIsDeleteFalse(TemplateMasterEntity templateMasterEntity,
			Long id);

	List<AdminTemplateModuleEntity> findAllByUserIdAndIsDeleteFalseAndTemplateModuleMaster_IdOrderByTemplateMapping_SortorderAsc(
			Long userId, Long templateModuleId);
	
	List<AdminTemplateModuleEntity> findAllByUserIdAndIsDeleteFalseOrderByTemplateMapping_SortorderAsc(Long userId);

	List<AdminTemplateModuleEntity> findAllByUserIdAndIsDeleteFalseOrderByTemplateModuleMaster_IdAscTemplateMapping_SortorderAsc(
			Long userId);

	@Query("SELECT atm "
			+ " FROM AdminTemplateModuleEntity atm "
			+ " JOIN FETCH atm.templateModuleMaster tmm "
			+ " WHERE atm.userId = :userId "
			+ " AND atm.isDelete = false "
			+ " AND tmm.id = 7 "
			+ " ORDER BY tmm.id ASC, atm.templateMapping.sortorder ASC ")
	List<AdminTemplateModuleEntity> getAllExclusiveThemeByUserIdAndIsDeleteFalse(@Param("userId") Long userId);
	
	Optional<AdminTemplateModuleEntity> findByIdAndTemplateMasterIdAndIsDeleteFalse(Long adminTemplateId, Long templateId);
	
	List<AdminTemplateModuleEntity> findAllByTemplateMasterAndIsDeleteFalse(TemplateMasterEntity templateMasterEntity);
	
	List<AdminTemplateModuleEntity> findByUserIdAndIsDeleteFalseAndIsActiveTrue(Long userId);
	
	List<AdminTemplateModuleEntity> findByIsDeleteFalseAndIsActiveTrue();

}
