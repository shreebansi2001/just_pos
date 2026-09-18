package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.request.dto.UnitMasterRequestDto;
import com.crmportal.response.dto.ParentUnitResponseDto;
import com.crmportal.response.dto.UnitMasterResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface UnitMasterMapper {

	UnitMasterEntity requestToEntity(@Valid UnitMasterRequestDto request);

	UnitMasterEntity updateEntityFromRequest(@Valid UnitMasterRequestDto request, @MappingTarget UnitMasterEntity entity);

	UnitMasterResponseDto entityToResponse(UnitMasterEntity entity);

	ParentUnitResponseDto entityToNewResponse(UnitMasterEntity unitMasterEntity);

}
