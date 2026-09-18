package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.MenuItemRawMaterialEntity;
import com.crmportal.response.dto.MenuItemRawMaterialsResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface MenuItemRawMaterialMapper {

	MenuItemRawMaterialsResponseDto entityToResponse(MenuItemRawMaterialEntity menuItemRawMaterialEntity);

}
