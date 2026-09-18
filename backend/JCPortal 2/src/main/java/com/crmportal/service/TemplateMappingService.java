package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.TemplateMappingRequestDto;
import com.crmportal.response.dto.TemplateMappingResponseDto;

@Service
public interface TemplateMappingService {

	TemplateMappingResponseDto addOrUpdateTemplateMapping(TemplateMappingRequestDto request, Long id);

	Boolean deleteTemplateMappingById(Long id);

	TemplateMappingResponseDto getTemplateMappingById(Long id);

	List<TemplateMappingResponseDto> getAllTemplateMapping(Long templateModuleId);

}
