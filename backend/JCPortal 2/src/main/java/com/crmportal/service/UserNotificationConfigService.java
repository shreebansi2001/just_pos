package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.UpdateUserNotificationRequestDto;
import com.crmportal.request.dto.UserUpgradedModulePaymentRequestDto;
import com.crmportal.response.dto.UserNotificationConfigResponseDto;

@Service
public interface UserNotificationConfigService {

	Boolean isActive(Long userId, Boolean isActive, String otp, List<Long> moduleId);

	Boolean addUserNotification(UserUpgradedModulePaymentRequestDto request);

	Boolean updateUserNotification(UpdateUserNotificationRequestDto request);

	List<UserNotificationConfigResponseDto> getAllByUser(Long userId);

	UserNotificationConfigResponseDto getNotification(Long userId, Long upgradeModuleId);

}
