package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.TemplateModuleMasterRequestDto;
import com.crmportal.response.dto.TemplateModuleMasterResponseDto;

@Service
public interface TemplateModuleMasterService {

	TemplateModuleMasterResponseDto addOrUpdateTemplateModuleMaster(TemplateModuleMasterRequestDto request, Long id);
	
	List<TemplateModuleMasterResponseDto> getAllTemplateModuleMaster();
	
	Boolean deleteTemplateModuleById(Long id);
	
	TemplateModuleMasterResponseDto getTemplateModuleById(Long id);
	
	Boolean updateTemplateModuleStatusById(Long id, Boolean status);
}
