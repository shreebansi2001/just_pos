package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.CityMasterEntity;
import com.crmportal.request.dto.CityMasterRequestDto;
import com.crmportal.response.dto.CityMasterResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface CityMasterMapper {

	CityMasterEntity requestToEntity(@Valid CityMasterRequestDto request);

	CityMasterResponseDto entityToResponse(CityMasterEntity entity);

}
