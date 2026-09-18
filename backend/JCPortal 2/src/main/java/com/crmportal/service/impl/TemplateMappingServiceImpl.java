package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.TemplateMappingEntity;
import com.crmportal.entity.TemplateModuleMasterEntity;
import com.crmportal.repository.InquiryRepository;
import com.crmportal.repository.TemplateMappingRepository;
import com.crmportal.repository.TemplateModuleMasterRepository;
import com.crmportal.request.dto.TemplateMappingRequestDto;
import com.crmportal.response.dto.TemplateMappingResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.TemplateMappingService;

@Service
public class TemplateMappingServiceImpl implements TemplateMappingService {

    private final InquiryRepository inquiryRepository;

	@Autowired
	TemplateModuleMasterRepository templateModuleMasterRepository;
	
	@Autowired
	TemplateMappingRepository templateMappingRepository;
	
	@Autowired
	CommonService commonService;

    TemplateMappingServiceImpl(InquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }
	
	@Override
	public TemplateMappingResponseDto addOrUpdateTemplateMapping(TemplateMappingRequestDto request, Long id) {
		
		TemplateModuleMasterEntity moduleMasterEntity = templateModuleMasterRepository.findByIdAndIsDeleteFalse(request.getTemplateModuleId())
				.orElseThrow(() -> new RuntimeException("Template Module is not found with id : " + request.getTemplateModuleId()));

		Optional<TemplateMappingEntity> dbEntity = templateMappingRepository.findByNameEnglishAndIsDeleteFalseAndTemplateModule(request.getNameEnglish(),moduleMasterEntity);
		
		
		TemplateMappingEntity entity = new TemplateMappingEntity();
		
		if(id == -1) {
			if(dbEntity.isPresent()) 
				throw new RuntimeException("This name is already exists");
			entity.setNameEnglish(request.getNameEnglish());
			entity.setNameHindi(request.getNameHindi());
			entity.setNameGujarati(request.getNameGujarati());
			entity.setTemplateModule(moduleMasterEntity);
			entity.setSortorder(request.getSortorder());
			entity.setNamePlateType(request.getNamePlateType());
			entity.setIsDate(request.getIsDate());
		} else {
			entity = templateMappingRepository.findByIdAndIsDeleteFalse(id);
			if(dbEntity.isPresent() && dbEntity.get().getId() != id) 				
				throw new RuntimeException("This name is already exists");
			
			entity.setUpdateAt(commonService.getCurrentDateTime());
			entity.setNameEnglish(request.getNameEnglish());
			entity.setNameHindi(request.getNameHindi());
			entity.setNameGujarati(request.getNameGujarati());
			entity.setTemplateModule(moduleMasterEntity);
			entity.setSortorder(request.getSortorder());
			entity.setNamePlateType(request.getNamePlateType());
			entity.setIsDate(request.getIsDate());

		}
		entity = templateMappingRepository.save(entity);
		
		TemplateMappingResponseDto responseDto = entityToResponse(entity);
		
		return responseDto;
	}
	
	@Override
	public Boolean deleteTemplateMappingById(Long id) {
		
		TemplateMappingEntity entity = Optional.ofNullable(templateMappingRepository.findByIdAndIsDeleteFalse(id))
				.orElseThrow(() -> new RuntimeException("Template Mapping is not found with id : " + id));
		
		entity.setIsDelete(true);
		templateMappingRepository.save(entity);
		
		return true;
	}

	@Override
	public TemplateMappingResponseDto getTemplateMappingById(Long id) {
		
		TemplateMappingEntity entity = Optional.ofNullable(templateMappingRepository.findByIdAndIsDeleteFalse(id))
				.orElseThrow(() -> new RuntimeException("Template Mapping is not found with id : " + id));
		
		TemplateMappingResponseDto responseDto = entityToResponse(entity);
		responseDto.setSortorder(entity.getSortorder());
		return responseDto;
	}
	
	@Override
	public List<TemplateMappingResponseDto> getAllTemplateMapping(Long templateModuleId) {
		List<TemplateMappingEntity> entities = new ArrayList<>();
		if(templateModuleId == null || templateModuleId == 0) {			
			entities = templateMappingRepository.findByIsDeleteFalse();
		} else {
			entities = templateMappingRepository.findByTemplateModuleIdAndIsDeleteFalse(templateModuleId);
		}
		
		return entities.stream().map(this::entityToResponse).collect(Collectors.toList());
	}
	
	private TemplateMappingResponseDto entityToResponse(TemplateMappingEntity entity) {
		
		TemplateMappingResponseDto responseDto = new TemplateMappingResponseDto();
		
		responseDto.setId(entity.getId());
		responseDto.setNameEnglish(entity.getNameEnglish());
		responseDto.setNameHindi(entity.getNameHindi());
		responseDto.setNameGujarati(entity.getNameGujarati());
		responseDto.setTemplateModuleId(entity.getTemplateModule().getId());
		responseDto.setTemplateModuleNameEnglish(entity.getTemplateModule().getNameEnglish());
		responseDto.setTemplateModuleNameHindi(entity.getTemplateModule().getNameHindi());
		responseDto.setTemplateModuleNameGujarati(entity.getTemplateModule().getNameGujarati());
		responseDto.setSortorder(entity.getSortorder());
		responseDto.setNamePlateType(entity.getNamePlateType());
		responseDto.setIsDate(entity.getIsDate());
		return responseDto;
	}
	
}
