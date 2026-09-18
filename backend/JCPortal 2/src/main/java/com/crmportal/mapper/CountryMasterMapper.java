package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.CountryMasterEntity;
import com.crmportal.request.dto.CountryMasterRequestDto;
import com.crmportal.response.dto.CountryMasterResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface CountryMasterMapper {

	CountryMasterEntity requestToEntity(@Valid CountryMasterRequestDto request);

	CountryMasterResponseDto entityToResponse(CountryMasterEntity entity);

}
