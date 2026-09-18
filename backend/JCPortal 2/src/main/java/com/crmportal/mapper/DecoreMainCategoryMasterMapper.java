package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.DecoreMainCategoryMasterEntity;
import com.crmportal.request.dto.DecoreMainCategoryMasterRequestDto;
import com.crmportal.response.dto.DecoreMainCategoryMasterResponseDto;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface DecoreMainCategoryMasterMapper {

	@Mapping(target = "imagePath", ignore = true)
	DecoreMainCategoryMasterEntity requestToEntity(@Valid DecoreMainCategoryMasterRequestDto request);

	@Mapping(target = "imagePath", ignore = true)
	void updateEntityFromRequest(DecoreMainCategoryMasterRequestDto request,
			@MappingTarget DecoreMainCategoryMasterEntity entity);

	DecoreMainCategoryMasterResponseDto entityToResponse(DecoreMainCategoryMasterEntity entity);

}
