package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.ExpenseDetailEntity;
import com.crmportal.request.dto.ExpenseDetailRequestDto;
import com.crmportal.response.dto.ExpenseDetailResponseDto;
import com.crmportal.utility.DateMapper;

@Mapper(
		componentModel = "spring",
		uses = { DateMapper.class },
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
		)
public interface ExpenseDetailMapper {

	@Mapping(target = "expenseDate", source = "expenseDate", qualifiedByName = "dateStringToDateTime")
	ExpenseDetailEntity requestToEntity(ExpenseDetailRequestDto dto);

	@Mapping(target = "expenseDate", source = "expenseDate", qualifiedByName = "dateStringToDateTime")
	ExpenseDetailEntity updateEntityFromRequest(ExpenseDetailRequestDto dto, @MappingTarget ExpenseDetailEntity detailEntity);

	@Mapping(target = "expenseDate", source = "expenseDate", qualifiedByName = "dateTimeWithSecondToString")
	ExpenseDetailResponseDto entityToResponse(ExpenseDetailEntity detailEntity);

}
