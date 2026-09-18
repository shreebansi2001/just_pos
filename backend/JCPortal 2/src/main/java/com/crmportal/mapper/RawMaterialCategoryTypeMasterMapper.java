package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.RawMaterialCategoryTypeMasterEntity;
import com.crmportal.request.dto.RawMaterialCategoryTypeMasterRequestDto;
import com.crmportal.response.dto.RawMaterialCategoryTypeMasterResponseDto;

@Mapper(
	    componentModel = "spring",
	    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
	    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
	)
public interface RawMaterialCategoryTypeMasterMapper {

	RawMaterialCategoryTypeMasterEntity requestToEntity(@Valid RawMaterialCategoryTypeMasterRequestDto request);

	RawMaterialCategoryTypeMasterEntity updateEntityFromRequest(@Valid RawMaterialCategoryTypeMasterRequestDto request,
		@MappingTarget	RawMaterialCategoryTypeMasterEntity entity);

	RawMaterialCategoryTypeMasterResponseDto entityToResponse(RawMaterialCategoryTypeMasterEntity entity);

}
