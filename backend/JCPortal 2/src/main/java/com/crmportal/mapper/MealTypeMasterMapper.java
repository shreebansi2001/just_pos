package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.MealTypeMasterEntity;
import com.crmportal.request.dto.MealTypeMasterRequestDto;
import com.crmportal.response.dto.MealTypeMasterResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface MealTypeMasterMapper {

	MealTypeMasterEntity requestToEntity(@Valid MealTypeMasterRequestDto request);

	void updateEntityFromRequest(@Valid MealTypeMasterRequestDto request, @MappingTarget MealTypeMasterEntity entity);

	MealTypeMasterResponseDto entityToResponse(MealTypeMasterEntity entity);

}
