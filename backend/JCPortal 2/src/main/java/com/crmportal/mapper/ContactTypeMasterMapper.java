package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.ContactTypeMasterEntity;
import com.crmportal.request.dto.ContactTypeMasterRequestDto;
import com.crmportal.response.dto.ContactTypeMasterResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface ContactTypeMasterMapper {

	ContactTypeMasterEntity requestToEntity(@Valid ContactTypeMasterRequestDto request);

	ContactTypeMasterEntity updateEntityFromRequest(@Valid ContactTypeMasterRequestDto request,
		@MappingTarget	ContactTypeMasterEntity entity);

	ContactTypeMasterResponseDto entityToResponse(ContactTypeMasterEntity entity);

}
