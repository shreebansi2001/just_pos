package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.PlansRequestDto;
import com.crmportal.response.dto.PlansResponseDto;

@Service
public interface PlansService {

	PlansResponseDto addOrUpdatePlans(@Valid PlansRequestDto request, long parseLong);

	List<PlansResponseDto> getAllPlans();

	PlansResponseDto getPlansById(Long id);

	Boolean deletePlanById(Long id);

	List<PlansResponseDto> getAllPlansByBillingCycle(String billingCycle);

}
