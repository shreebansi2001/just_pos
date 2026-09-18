package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.ExpenseManagementEntity;
import com.crmportal.request.dto.ExpenseManagementRequestDto;
import com.crmportal.response.dto.ExpenseManagementResponseDto;
import com.crmportal.utility.DateMapper;

@Mapper(
		componentModel = "spring",
		uses = DateMapper.class,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface ExpenseManagementMapper {

	@Mapping(source = "date", target = "date", qualifiedByName = "stringToDate", defaultExpression = "java(null)")
	ExpenseManagementEntity requestToEntity(ExpenseManagementRequestDto request);

	@Mapping(source = "date", target = "date", qualifiedByName = "stringToDate", defaultExpression = "java(null)")
	void updateEntityFromRequest(ExpenseManagementRequestDto request, @MappingTarget ExpenseManagementEntity entity);

	ExpenseManagementResponseDto entityToResponse(ExpenseManagementEntity entity);

}
