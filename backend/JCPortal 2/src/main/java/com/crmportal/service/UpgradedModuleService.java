package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.UpgradedModuleRequestDto;
import com.crmportal.request.dto.UserUpgradedModulePaymentRequestDto;
import com.crmportal.response.dto.UpgradedModuleResponseDto;

@Service
public interface UpgradedModuleService {

	Boolean addOrUpgradedModule(UpgradedModuleRequestDto request);

	List<UpgradedModuleResponseDto> getAll(Boolean isActive, Long userId,Boolean isConfig);

	Boolean addUserUpgradeModule(UserUpgradedModulePaymentRequestDto request);

	Boolean isActive(Long userId, Boolean isActive, String otp, List<Long> moduleId);

	Boolean deleteById(Long id);

	Boolean extendDate(Long userId, List<Long> moduleId, String endDate, String otp);

}
