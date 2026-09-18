package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.MenuSubCategoryMasterEntity;
import com.crmportal.request.dto.MenuSubCategoryMasterRequestDto;
import com.crmportal.response.dto.MenuSubCategoryMasterResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface MenuSubCategoryMasterMapper {

	MenuSubCategoryMasterEntity requestToEntity(@Valid MenuSubCategoryMasterRequestDto request);

	void updateEntityFromRequest(@Valid MenuSubCategoryMasterRequestDto request, @MappingTarget MenuSubCategoryMasterEntity entity);

	MenuSubCategoryMasterResponseDto entityToResponse(MenuSubCategoryMasterEntity saved);

}
