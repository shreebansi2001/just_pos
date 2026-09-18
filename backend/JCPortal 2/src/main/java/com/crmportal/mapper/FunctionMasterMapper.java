package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.FunctionMasterEntity;
import com.crmportal.request.dto.FunctionMasterRequestDto;
import com.crmportal.response.dto.FunctionMasterResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface FunctionMasterMapper {

	FunctionMasterEntity requestToEntity(@Valid FunctionMasterRequestDto request);

	void updateEntityFromRequest(@Valid FunctionMasterRequestDto request, @MappingTarget FunctionMasterEntity entity);

	FunctionMasterResponseDto entityToResponse(FunctionMasterEntity entity);

}
