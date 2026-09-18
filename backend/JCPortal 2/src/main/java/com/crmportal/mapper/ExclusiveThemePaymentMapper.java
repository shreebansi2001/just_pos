package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.ExclusiveThemePaymentEntity;
import com.crmportal.request.dto.ExclusivePaymentRequestDto;
import com.crmportal.request.dto.ExclusiveThemePaymentRequestDto;
import com.crmportal.response.dto.ExclusiveThemePaymentResponseDto;

@Mapper(
		componentModel = "spring",
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
		)
public interface ExclusiveThemePaymentMapper {


	ExclusiveThemePaymentEntity updateEntityFromRequest(ExclusiveThemePaymentEntity exclusiveThemePaymentEntity);

	ExclusiveThemePaymentResponseDto entityToResponse(ExclusiveThemePaymentEntity entity);

	ExclusiveThemePaymentEntity requestToEntity(ExclusivePaymentRequestDto request);

}
