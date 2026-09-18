package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.request.dto.MenuCategoryMasterRequestDto;
import com.crmportal.response.dto.MenuCategoryMasterResponseDto;
import com.crmportal.response.dto.MenuCategoryMenuPreparationResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface MenuCategoryMasterMapper {

	MenuCategoryMasterResponseDto entityToResponse(MenuCategoryMasterEntity saved);

	MenuCategoryMasterEntity requestToEntity(@Valid MenuCategoryMasterRequestDto request);

	void updateEntityFromRequest(@Valid MenuCategoryMasterRequestDto request, @MappingTarget MenuCategoryMasterEntity entity);

	MenuCategoryMenuPreparationResponseDto entityToResponses(MenuCategoryMasterEntity entity);

}
