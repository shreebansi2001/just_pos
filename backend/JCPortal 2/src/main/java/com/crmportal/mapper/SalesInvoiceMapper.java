package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.SalesInvoiceEntity;
import com.crmportal.request.dto.SalesInvoiceRequestDto;
import com.crmportal.response.dto.BankDetailsResponseDto;
import com.crmportal.response.dto.SalesInvoiceResponseDto;
import com.crmportal.utility.DateMapper;

@Mapper(
		componentModel = "spring",
				uses = DateMapper.class,
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
		)
public interface SalesInvoiceMapper {

	@Mapping(source = "paymentDate", target = "paymentDate", qualifiedByName = "dateStringToDateTime")
	SalesInvoiceEntity requestToEntity(SalesInvoiceRequestDto request);

	@Mapping(source = "paymentDate", target = "paymentDate", qualifiedByName = "dateStringToDateTime")
	SalesInvoiceEntity updateEntityFromRequest(@MappingTarget SalesInvoiceEntity salesInvoiceEntity, SalesInvoiceRequestDto request);

	SalesInvoiceResponseDto entityToResponse(SalesInvoiceEntity entity);

}
