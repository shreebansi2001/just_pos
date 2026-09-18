package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.NameplateEntity;
import com.crmportal.request.NameplateRequestDto;
import com.crmportal.response.dto.EventFunctionNameplateResponseDto;

@Mapper(
		componentModel = "spring",
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface NameplateMapper {

	NameplateEntity requestToEntity(NameplateRequestDto dto);

	EventFunctionNameplateResponseDto entityToResponse(NameplateEntity entity);

	NameplateEntity updateEntityFromRequest(@MappingTarget NameplateEntity entity, NameplateRequestDto dto);

}
