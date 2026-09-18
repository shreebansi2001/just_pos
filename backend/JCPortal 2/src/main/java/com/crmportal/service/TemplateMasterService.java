package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.TemplateMasterRequestDto;
import com.crmportal.response.dto.TemplateMasterResponseDto;

@Service
public interface TemplateMasterService {

	TemplateMasterResponseDto addOrUpdateTemplateMaster(TemplateMasterRequestDto request, List<MultipartFile> modulePages, MultipartFile dummyPdf, List<MultipartFile> namePlateBg, Long id);
	
	List<TemplateMasterResponseDto> getAllTemplateMaster();
	
	TemplateMasterResponseDto getTemplateMasterById(Long id);
	
	Boolean deleteTemplateById(Long id);
	
	Boolean updateTemplateMasterStatusById(Long id, Boolean status);
	
	List<TemplateMasterResponseDto> getAllTemplateMasterByModuleId(Long id, Boolean isNameplate,Long userId);
	
	List<TemplateMasterResponseDto> getAllTemplateMasterByModuleIdAndUserId(Long moduleId, Long userId);

	List<TemplateMasterResponseDto> getAllTemplateMasterByTemplateMappingId(Long id);
}
