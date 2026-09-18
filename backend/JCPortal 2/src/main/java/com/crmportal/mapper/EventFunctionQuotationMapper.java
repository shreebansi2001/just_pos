package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.EventFunctionQuotationEntity;
import com.crmportal.request.dto.EventFunctionQuotationRequestDto;
import com.crmportal.response.dto.EventFunctionQuotationResponseDto;
import com.crmportal.utility.DateMapper;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface EventFunctionQuotationMapper {

	EventFunctionQuotationEntity requestToEntity(@Valid EventFunctionQuotationRequestDto request);

	EventFunctionQuotationEntity updateEntityFromRequest(@Valid EventFunctionQuotationRequestDto request,
		@MappingTarget	EventFunctionQuotationEntity eventFunctionQuotationEntity);

	EventFunctionQuotationResponseDto entityToResponseDto(EventFunctionQuotationEntity eventFunctionQuotationEntity);


}
