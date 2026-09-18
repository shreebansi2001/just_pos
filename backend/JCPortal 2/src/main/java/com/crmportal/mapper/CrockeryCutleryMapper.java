package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.CrockeryCutleryEntity;
import com.crmportal.request.dto.CrockeryCutleryRequestDto;
import com.crmportal.response.dto.CrockeryCutleryResponseDto;

@Mapper(
		componentModel = "spring",
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
		)
public interface CrockeryCutleryMapper {

	CrockeryCutleryResponseDto entityToResponse(CrockeryCutleryEntity entitie);

	CrockeryCutleryEntity requestToEntity(CrockeryCutleryRequestDto dto);

	CrockeryCutleryEntity updateEntityFromRequest(@MappingTarget CrockeryCutleryEntity entity, CrockeryCutleryRequestDto dto);

}
