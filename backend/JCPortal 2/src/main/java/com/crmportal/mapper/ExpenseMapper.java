package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.ExpenseEntity;
import com.crmportal.request.dto.ExpenseRequestDto;
import com.crmportal.response.dto.ExpenseResponseDto;
import com.crmportal.utility.DateMapper;

@Mapper(
		componentModel = "spring",
		uses = { DateMapper.class },
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
		)
public interface ExpenseMapper {

	@Mapping(target = "fromDate", source = "fromDate", qualifiedByName = "dateStringToDateTime")
	@Mapping(target = "toDate", source = "toDate", qualifiedByName = "dateStringToDateTime")
	@Mapping(target = "dueDate", source = "dueDate", qualifiedByName = "dateStringToDateTime")
	@Mapping(target = "paidDate", source = "paidDate", qualifiedByName = "dateStringToDateTime")
	ExpenseEntity requestToEntity(ExpenseRequestDto request);

	@Mapping(target = "fromDate", source = "request.fromDate", qualifiedByName = "dateStringToDateTime")
	@Mapping(target = "toDate", source = "request.toDate", qualifiedByName = "dateStringToDateTime")
	@Mapping(target = "dueDate", source = "dueDate", qualifiedByName = "dateStringToDateTime")
	@Mapping(target = "paidDate", source = "paidDate", qualifiedByName = "dateStringToDateTime")
	ExpenseEntity updateEntityFromRequest(ExpenseRequestDto request, @MappingTarget ExpenseEntity expenseEntity);

	@Mapping(target = "fromDate", source = "fromDate", qualifiedByName = "dateTimeWithSecondToString")
	@Mapping(target = "toDate", source = "toDate", qualifiedByName = "dateTimeWithSecondToString")
	@Mapping(target = "dueDate", source = "dueDate", qualifiedByName = "dateTimeWithSecondToString")
	@Mapping(target = "paidDate", source = "paidDate", qualifiedByName = "dateTimeWithSecondToString")
	ExpenseResponseDto entityToResponse(ExpenseEntity expenseEntity);

}
