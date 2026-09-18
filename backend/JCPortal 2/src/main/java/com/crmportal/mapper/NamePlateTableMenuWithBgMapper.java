package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.NamePlateTableMenuWithBgEntity;
import com.crmportal.entity.NameplateEntity;
import com.crmportal.request.NameplateRequestDto;
import com.crmportal.request.dto.NamePlateTableMenuWithBgRequestDto;
import com.crmportal.response.dto.NamePlateTableMenuWithBgResponseDto;

@Mapper(
		componentModel = "spring",
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface NamePlateTableMenuWithBgMapper {

	NamePlateTableMenuWithBgEntity requestToEntity(NamePlateTableMenuWithBgRequestDto request);
	
	NamePlateTableMenuWithBgResponseDto entityToResponse(NamePlateTableMenuWithBgEntity request);
	
	NamePlateTableMenuWithBgEntity updateEntityFromRequest(@MappingTarget NamePlateTableMenuWithBgEntity entity, NamePlateTableMenuWithBgRequestDto dto);
}
