package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.ReportConfigurationEntity;
import com.crmportal.entity.TemplateMappingEntity;
import com.crmportal.entity.TemplateModuleMasterEntity;
import com.crmportal.mapper.ReportConfigurationMapper;
import com.crmportal.repository.ConfigurationUtilEntityRepository;
import com.crmportal.repository.ReportConfigurationRepository;
import com.crmportal.repository.TemplateMappingRepository;
import com.crmportal.repository.TemplateModuleMasterRepository;
import com.crmportal.request.dto.ReportConfigurationRequestDto;
import com.crmportal.response.dto.ReportConfigurationResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.ReportConfigurationService;

@Service
public class ReportConfigurationServiceImpl implements ReportConfigurationService {

    private final ConfigurationUtilEntityRepository configurationUtilEntityRepository;

	@Autowired
	TemplateMappingRepository templateMappingRepository;

	@Autowired
	ReportConfigurationMapper reportConfigurationMapper;

	@Autowired
	ReportConfigurationRepository reportConfigurationRepository;

	@Autowired
	TemplateModuleMasterRepository templateModuleMasterRepository;

	@Autowired
	CommonService commonService;

    ReportConfigurationServiceImpl(ConfigurationUtilEntityRepository configurationUtilEntityRepository) {
        this.configurationUtilEntityRepository = configurationUtilEntityRepository;
    }

	@Override
	public ReportConfigurationResponseDto addOrUpdateReportConfiguration(ReportConfigurationRequestDto request) {

		TemplateMappingEntity mappingEntity = Optional
				.ofNullable(templateMappingRepository.findByIdAndIsDeleteFalse(request.getTemplateMappingId()))
				.orElseThrow(() -> new RuntimeException(
						"Template mapping is not found with id : " + request.getTemplateMappingId()));

		TemplateModuleMasterEntity moduleMasterEntity = templateModuleMasterRepository
				.findByIdAndIsDeleteFalse(request.getTemplateModuleId()).orElseThrow(() -> new RuntimeException(
						"Template mapping is not found with id : " + request.getTemplateMappingId()));

		ReportConfigurationEntity entity = new ReportConfigurationEntity();

		if (request.getId() == -1) {
			entity = reportConfigurationMapper.requestToEntity(request);
			entity.setTemplateMapping(mappingEntity);
			entity.setTemplateModule(moduleMasterEntity);
		} else {
			entity = reportConfigurationRepository.findByIdAndIsDeleteFalse(request.getId()).orElseThrow(
					() -> new RuntimeException("Template mapping is not found with id : " + request.getId()));
			entity = reportConfigurationMapper.updateEntityFromRequest(request, entity);
			entity.setTemplateMapping(mappingEntity);
			entity.setTemplateModule(moduleMasterEntity);
			entity.setUpdateAt(commonService.getCurrentDateTime());
		}

		entity = reportConfigurationRepository.save(entity);
		ReportConfigurationResponseDto responseDto = reportConfigurationMapper.entityToResponse(entity);
		
		responseDto.setTemplateMappingId(entity.getTemplateMapping().getId());
		responseDto.setMappingNameEnglish(entity.getTemplateMapping().getNameEnglish());
		responseDto.setMappingNameHindi(entity.getTemplateMapping().getNameHindi());
		responseDto.setMappingNameGujarati(entity.getTemplateMapping().getNameGujarati());
		
		if(entity.getTemplateModule() != null)	 {				
			responseDto.setTemplateModuleId(entity.getTemplateModule().getId());
			responseDto.setModuleNameEnglish(entity.getTemplateModule().getNameEnglish());
			responseDto.setModuleNameHindi(entity.getTemplateModule().getNameHindi());
			responseDto.setModuleNameGujarati(entity.getTemplateModule().getNameGujarati());
		}

		return responseDto;
	}

	@Override
	public Boolean deleteReportConfiguration(Long id) {

		ReportConfigurationEntity entity = reportConfigurationRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Template mapping is not found with id : " + id));

		entity.setIsDelete(true);
		reportConfigurationRepository.save(entity);

		return true;
	}

	@Override
	public List<ReportConfigurationResponseDto> getAllReportConfiguration(
	        Long mappingId, Long moduleId, Integer isExtraCharges) {

	    List<ReportConfigurationResponseDto> responseDtos = new ArrayList<>();
	    List<ReportConfigurationEntity> entityList;

	    // ── Fetch by filters ──────────────────────────────────────────────────
	    if (mappingId != null && moduleId != null) {
	        entityList = reportConfigurationRepository
	                .findAllByTemplateMappingIdAndTemplateModuleIdAndIsDeleteFalse(mappingId, moduleId);
	    } else if (mappingId != null) {                         
	        entityList = reportConfigurationRepository
	                .findAllByTemplateMappingIdAndIsDeleteFalse(mappingId);
	    } else if (moduleId != null) {                           
	        entityList = reportConfigurationRepository
	                .findAllByTemplateModuleIdAndIsDeleteFalse(moduleId);
	    } else {
	        entityList = reportConfigurationRepository.findAllByIsDeleteFalse();
	    }

	    // ── Filter by isExtraCharges if provided ──────────────────────────────
	    if (isExtraCharges != null && entityList != null) {
	        final Integer filter = isExtraCharges;
	        entityList = entityList.stream()
	                .filter(e -> filter.equals(e.getIsExtraCharges()))
	                .collect(java.util.stream.Collectors.toList());
	    }

	    if (entityList != null && !entityList.isEmpty()) {
	        for (ReportConfigurationEntity entity : entityList) {
	            responseDtos.add(buildResponseDto(entity));
	        }
	    }

	    return responseDtos;
	}

	@Override
	public ReportConfigurationResponseDto getReportConfigurationById(Long id, Integer isExtraCharges) {

	    ReportConfigurationEntity entity = reportConfigurationRepository
	            .findByIdAndIsDeleteFalse(id)
	            .orElseThrow(() -> new RuntimeException(
	                    "Configuration not found with id: " + id));

	    // ── If filter provided, validate it matches ───────────────────────────
	    if (isExtraCharges != null && !isExtraCharges.equals(entity.getIsExtraCharges())) {
	        throw new RuntimeException(
	                "Configuration with id " + id + " does not match isExtraCharges=" + isExtraCharges);
	    }

	    return buildResponseDto(entity);
	}
	
	private ReportConfigurationResponseDto buildResponseDto(ReportConfigurationEntity entity) {

	    ReportConfigurationResponseDto dto = reportConfigurationMapper.entityToResponse(entity);

	    dto.setTemplateMappingId(entity.getTemplateMapping().getId());
	    dto.setMappingNameEnglish(entity.getTemplateMapping().getNameEnglish());
	    dto.setMappingNameHindi(entity.getTemplateMapping().getNameHindi());
	    dto.setMappingNameGujarati(entity.getTemplateMapping().getNameGujarati());
	    if (entity.getTemplateModule() != null) {
	        dto.setTemplateModuleId(entity.getTemplateModule().getId());
	        dto.setModuleNameEnglish(entity.getTemplateModule().getNameEnglish());
	        dto.setModuleNameHindi(entity.getTemplateModule().getNameHindi());
	        dto.setModuleNameGujarati(entity.getTemplateModule().getNameGujarati());
	    }

	    dto.setIsExtraCharges(entity.getIsExtraCharges()); // ← expose in response

	    return dto;
	}

}