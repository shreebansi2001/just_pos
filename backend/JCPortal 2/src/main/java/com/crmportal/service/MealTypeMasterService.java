package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.MealTypeMasterRequestDto;
import com.crmportal.response.dto.MealTypeMasterResponseDto;

@Service
public interface MealTypeMasterService {

	MealTypeMasterResponseDto addOrUpdateMealType(@Valid MealTypeMasterRequestDto request, long parseLong);

	List<MealTypeMasterResponseDto> getAllMealTypesByUserId(Long userId, String mealTypeName);

	MealTypeMasterResponseDto getMealTypeById(Long id);

	Boolean deleteMealTypeById(Long id);

}
