package com.crmportal.service;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.AdminTemplateModuleFontAndFontSizeRequestDto;
import com.crmportal.request.dto.AdminTemplateModuleRequestDTO;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;

@Service
public interface AdminTemplateModuleService {

	Boolean addOrUpdateAdminTemplateModule(List<AdminTemplateModuleRequestDTO> request, Long id);
	
	List<AdminTemplateModuleResponseDto> getAllAdminTemplateModule(Long userId,Long templateModuleId, Boolean isExclusive);
	
	AdminTemplateModuleResponseDto getAdminTemplateModuleById(Long id);
	
	AdminTemplateModuleResponseDto getAdminTemplateModuleById(Long id, Long userId);
	
	Boolean updateAdminTemplateModuleStatusById(Long id, Boolean status);
	
	Boolean deleteAdminTemplateModuleById(Long id);

	Integer allocateThemes(Long userId);

	Boolean updateAdminTemplateModuleFontAndFontSizeById(
			@Valid List<AdminTemplateModuleFontAndFontSizeRequestDto> request);
}
