package com.crmportal.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.request.dto.BankDetailsRequestDto;
import com.crmportal.response.dto.BankDetailsResponseDto;

@Mapper(componentModel = "spring",
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface BankDetailsMapper {

	@Mapping(target = "openingDate", expression = "java(parseDate(request.getOpeningDate()))")
	BankDetailsEntity requestToEntity(BankDetailsRequestDto request);

	@Mapping(target = "openingDate", expression = "java(parseDate(request.getOpeningDate()))")
	BankDetailsEntity updateEntityFromRequest(@MappingTarget BankDetailsEntity bankDetailsEntity, BankDetailsRequestDto request);

	@Mapping(target = "openingDate", dateFormat = "dd/MM/yyyy")
	BankDetailsResponseDto entityToResponse(BankDetailsEntity entity);
	
	default LocalDate parseDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(date, formatter);
    }

}
