package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.ReportConfigurationEntity;

@Repository
public interface ReportConfigurationRepository extends JpaRepository<ReportConfigurationEntity, Long> {

	Optional<ReportConfigurationEntity> findByIdAndIsDeleteFalse(Long id);

	List<ReportConfigurationEntity> findAllByIsDeleteFalse();

	List<ReportConfigurationEntity> findAllByTemplateMappingIdAndIsDeleteFalse(Long mappingId);

	List<ReportConfigurationEntity> findAllByTemplateMappingIdAndTemplateModuleIdAndIsDeleteFalse(Long mappingId,
			Long moduleId);

	List<ReportConfigurationEntity> findAllByTemplateModuleIdAndIsDeleteFalse(Long moduleId);

}
