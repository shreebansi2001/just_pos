package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.MenuItemAllocationConfigEntity;
import com.crmportal.request.dto.MenuAllocationChangeRequestDto;
import com.crmportal.request.dto.MenuItemAllocationConfigRequestDto;
import com.crmportal.request.dto.MenuItemMasterRequestDto;
import com.crmportal.response.dto.MenuItemAllocationConfigResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface MenuItemAllocationConfigMapper {

	MenuItemAllocationConfigEntity requestToEntity(@Valid MenuItemAllocationConfigRequestDto menuItemAllocationConfigRequestDto);

	MenuItemAllocationConfigEntity updateEntityFromRequest(
			MenuItemAllocationConfigRequestDto menuItemAllocationConfigRequest,@MappingTarget
			MenuItemAllocationConfigEntity allocationConfigEntity);

	MenuItemAllocationConfigResponseDto entityToResponse(MenuItemAllocationConfigEntity allocationConfigEntity);

	MenuItemAllocationConfigEntity updateEntityFromRequest(MenuAllocationChangeRequestDto menuAllocationChangeRequestDto,
			@MappingTarget MenuItemAllocationConfigEntity itemAllocationConfigEntity);

	MenuItemAllocationConfigEntity requestToEntity(MenuAllocationChangeRequestDto menuAllocationChangeRequestDto);

}
