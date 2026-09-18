package com.crmportal.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.UserTermsAndConditionRequestDto;
import com.crmportal.response.dto.UserTermsAndConditionResponseDto;

@Service
public interface UserTermsAndConditionService {

	Boolean addUpdate(UserTermsAndConditionRequestDto request);

	Map<String, Object> getAll(Long userId, String moduleName, Boolean isActive);

	Boolean delete(Long id);

	Boolean isActive(Long id, Boolean isActive);

}
