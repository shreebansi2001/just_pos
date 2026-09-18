package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.StateMasterEntity;
import com.crmportal.request.dto.StateMasterRequestDto;
import com.crmportal.response.dto.StateMasterResponseDto;

@Mapper(
	    componentModel = "spring",
	    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
	    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
	)
public interface StateMasterMappper {

	StateMasterEntity requestToEntity(StateMasterRequestDto request);

	StateMasterResponseDto entityToResponse(StateMasterEntity entity);

}
