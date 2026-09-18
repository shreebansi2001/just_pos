package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.InvoicePaymentHistoryEntity;
import com.crmportal.request.dto.InvoicePaymentHistoryRequestDto;
import com.crmportal.response.dto.InvoicePaymentHistoryResponseDto;

@Mapper(
		componentModel = "spring",
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
)
public interface InvoicePaymentHistoryMapper {

	InvoicePaymentHistoryEntity requestToEntity(InvoicePaymentHistoryRequestDto request);
	
	InvoicePaymentHistoryResponseDto entityToResponse(InvoicePaymentHistoryEntity entity);
}
