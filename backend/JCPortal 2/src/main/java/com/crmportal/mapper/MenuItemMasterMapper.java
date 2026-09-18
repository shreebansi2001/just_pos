package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.request.dto.MenuItemMasterRequestDto;
import com.crmportal.response.dto.MenuItemMasterResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface MenuItemMasterMapper {

	MenuItemMasterEntity requestToEntity(@Valid MenuItemMasterRequestDto request);

	void updateEntityFromRequest(@Valid MenuItemMasterRequestDto request, @MappingTarget MenuItemMasterEntity entity);

	MenuItemMasterResponseDto entityToResponse(MenuItemMasterEntity entity);

}
