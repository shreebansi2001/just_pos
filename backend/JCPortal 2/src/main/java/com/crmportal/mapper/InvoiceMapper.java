package com.crmportal.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.InvoiceEntity;
import com.crmportal.request.dto.InvoiceRequestDTO;
import com.crmportal.response.dto.InvoiceResponseDto;
import com.crmportal.response.dto.SuperAdminInvoiceResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface InvoiceMapper {

	InvoiceResponseDto entityToResponse(InvoiceEntity entity);
	
	InvoiceEntity requestToEntity(InvoiceRequestDTO request);
	
	SuperAdminInvoiceResponseDto toResponse(InvoiceEntity entity);
	
	List<SuperAdminInvoiceResponseDto> entitiesToResponses(List<InvoiceEntity> entity);

}
