package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.EventRemarkMasterEntity;
import com.crmportal.request.dto.EventRemarkMasterRequestDto;
import com.crmportal.response.dto.EventRemarkMasterResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface EventRemarkMasterMapper {

	EventRemarkMasterEntity requestToEntity(@Valid EventRemarkMasterRequestDto request);

	void updateEntityFromRequest(@Valid EventRemarkMasterRequestDto request, @MappingTarget EventRemarkMasterEntity entity);

	EventRemarkMasterResponseDto entityToResponse(EventRemarkMasterEntity entity);

}
