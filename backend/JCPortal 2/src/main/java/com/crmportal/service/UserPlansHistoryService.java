package com.crmportal.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.UserPlansHistoryRequestDto;
import com.crmportal.response.dto.UserPlansHistoryResponseDto;

@Service
public interface UserPlansHistoryService {

	Boolean addUserPlan(@Valid UserPlansHistoryRequestDto request);

	List<UserPlansHistoryResponseDto> getPlanHistoryByUser(Long userId);

	Map<String, Object> getRenewalCustomerInfo(LocalDateTime startDate, LocalDateTime endDate, boolean isActive);

	Boolean updateUserPlanDate(String date, Long userId, String otp);

}
