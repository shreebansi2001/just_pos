package com.crmportal.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.EventAdvancePaymentEntity;
import com.crmportal.request.dto.EventAdvancePaymentRequestDto;
import com.crmportal.response.dto.EventAdvancePaymentResponseDto;

@Mapper(
		componentModel = "spring",
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
)
public interface EventAdvancePaymentMapper {

		void updateEntity(@MappingTarget EventAdvancePaymentEntity entity, EventAdvancePaymentRequestDto request);

	    EventAdvancePaymentEntity toEntity(EventAdvancePaymentRequestDto request);

	    EventAdvancePaymentResponseDto toResponseDto(EventAdvancePaymentEntity entity);

	    List<EventAdvancePaymentResponseDto> toResponseDtoList(
	            List<EventAdvancePaymentEntity> entities);

	    default LocalDate stringToLocalDate(String date) {
	        return date == null || date.isEmpty()
	                ? null
	                : LocalDate.parse(
	                        date,
	                        DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	    }

	    default String localDateToString(LocalDate date) {
	        return date == null
	                ? null
	                : date.format(
	                        DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	    }
}
