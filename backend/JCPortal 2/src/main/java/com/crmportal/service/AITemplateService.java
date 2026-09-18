package com.crmportal.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.AITemplateRequestDto;
import com.crmportal.request.dto.GenerateAIRequestDto;
import com.crmportal.request.dto.UserUpgradedModulePaymentRequestDto;
import com.crmportal.response.dto.AITemplateResponseDto;

@Service
public interface AITemplateService {

	Boolean addOrUpdate(AITemplateRequestDto request);

	List<AITemplateResponseDto> getAll(Boolean isActive);

	AITemplateResponseDto getById(Long id);

	Boolean deleteById(Long id);

	Boolean isActive(Long id, Boolean isActive);

	Boolean extendDate(Long userId, List<Long> moduleId, String endDate, String otp);

	Boolean isActiveUserAI(Long userId, Boolean isActive, String otp, List<Long> moduleId);

	Boolean addUserAiTemplate(UserUpgradedModulePaymentRequestDto request);

	Map<String, Object> generateAIData(GenerateAIRequestDto request);

}
