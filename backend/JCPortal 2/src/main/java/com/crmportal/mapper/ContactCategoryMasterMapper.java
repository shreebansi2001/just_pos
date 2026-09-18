package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.ContactCategoryMasterEntity;
import com.crmportal.request.dto.ContactCategoryMasterRequestDto;
import com.crmportal.response.dto.ContactCategoryMasterResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface ContactCategoryMasterMapper {

	ContactCategoryMasterEntity requestToEntity(@Valid ContactCategoryMasterRequestDto request);

	ContactCategoryMasterResponseDto entityToResponse(ContactCategoryMasterEntity entity);

}
