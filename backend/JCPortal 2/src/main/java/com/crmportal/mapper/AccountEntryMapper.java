package com.crmportal.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.AccountEntryEntity;
import com.crmportal.request.dto.AccountEntryRequestDto;
import com.crmportal.response.dto.AccountEntryResponseDto;

@Mapper(
		componentModel = "spring",
		uses = { CashAccountMapper.class },
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
)
public interface AccountEntryMapper {

	@Mapping(target = "date", expression = "java(parseDate(request.getDate()))")
	AccountEntryEntity requestToEntity(AccountEntryRequestDto request);
	
	@Mapping(target = "date", dateFormat = "dd/MM/yyyy")
	@Mapping(target = "createdAt", dateFormat = "dd/MM/yyyy hh:mm:s")
	@Mapping(target = "updatedAt", dateFormat = "dd/MM/yyyy hh:mm:s")
	AccountEntryResponseDto entityToResponse(AccountEntryEntity entity);
	
	default LocalDate parseDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(date, formatter);
    }
}
