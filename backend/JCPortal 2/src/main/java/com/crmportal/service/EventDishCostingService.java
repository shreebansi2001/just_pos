package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.response.dto.EventDishCostingResponseDto;
import com.crmportal.response.dto.RawMaterialCategoryRateResponseDto;

@Service
public interface EventDishCostingService {
	EventDishCostingResponseDto geDishCosting(Long eventId, Long eventFunctionId, Boolean isRawMaterialDone, Boolean isMenuAllocationDone);

	List<RawMaterialCategoryRateResponseDto> getRawMaterialTotalCategoryWise(Long eventId, Long eventFunctionId, Boolean isRawMaterialDone, Boolean isMenuAllocationDone);
}
