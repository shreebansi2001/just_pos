package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.EventTypeMasterEntity;
import com.crmportal.request.dto.EventTypeMasterRequestDto;
import com.crmportal.response.dto.EventTypeMasterResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface EventTypeMasterMapper {

	EventTypeMasterEntity requestToEntity(@Valid EventTypeMasterRequestDto request);

	void updateEntityFromRequest(@Valid EventTypeMasterRequestDto request, @MappingTarget EventTypeMasterEntity entity);

	EventTypeMasterResponseDto entityToResponse(EventTypeMasterEntity entity);

}
