package com.crmportal.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.InvoiceEntity;
import com.crmportal.entity.InvoiceItemsEntity;
import com.crmportal.request.dto.InvoiceItemsRequestDto;
import com.crmportal.response.dto.SuperAdminInvoiceItemsResponseDto;
import com.crmportal.response.dto.SuperAdminInvoiceResponseDto;

@Mapper(
		componentModel = "spring",
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
)
public interface InvoiceItemsMapper {

	InvoiceItemsEntity requestToEntity(InvoiceItemsRequestDto request);
	
	@Mapping(target = "createdAt", dateFormat = "dd/MM/yyyy HH:mm:ss")
	SuperAdminInvoiceItemsResponseDto entityToResponse(InvoiceItemsEntity entity);
	
	@Mapping(target = "createdAt", dateFormat = "dd/MM/yyyy HH:mm:ss")
    List<SuperAdminInvoiceResponseDto> entitiesToResponses(List<InvoiceEntity> entities);
}
