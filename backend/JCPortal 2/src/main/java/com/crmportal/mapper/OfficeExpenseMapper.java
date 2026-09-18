package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.OfficeExpenseEntity;
import com.crmportal.request.dto.OfficeExpenseRequestDto;
import com.crmportal.response.dto.OfficeExpenseResponseDto;
import com.crmportal.utility.DateMapper;

@Mapper(
		componentModel = "spring",
		uses = {DateMapper.class},
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
		)
public interface OfficeExpenseMapper {

	@Mapping(target = "expenseDate", source = "expenseDate", qualifiedByName = "dateStringToDateTime")
	@Mapping(target = "dueDate", source = "dueDate", qualifiedByName = "dateStringToDateTime")
	@Mapping(target = "paidDate", source = "paidDate", qualifiedByName = "dateStringToDateTime")
	OfficeExpenseEntity requestToEntity(OfficeExpenseRequestDto request);

	@Mapping(target = "expenseDate", source = "expenseDate", qualifiedByName = "dateStringToDateTime")
	@Mapping(target = "dueDate", source = "dueDate", qualifiedByName = "dateStringToDateTime")
	@Mapping(target = "paidDate", source = "paidDate", qualifiedByName = "dateStringToDateTime")
	OfficeExpenseEntity updateEntityFromRequest(OfficeExpenseRequestDto request,
			@MappingTarget OfficeExpenseEntity officeExpenseEntity);

	@Mapping(target = "expenseDate", source = "expenseDate", qualifiedByName = "dateTimeWithSecondToString")
	@Mapping(target = "dueDate", source = "dueDate", qualifiedByName = "dateTimeWithSecondToString")
	@Mapping(target = "paidDate", source = "paidDate", qualifiedByName = "dateTimeWithSecondToString")
	OfficeExpenseResponseDto entityToResponse(OfficeExpenseEntity officeExpenseEntity);

}
