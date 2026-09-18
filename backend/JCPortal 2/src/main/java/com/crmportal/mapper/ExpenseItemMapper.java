package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.ExpenseItemEntity;
import com.crmportal.request.dto.ExpenseItemRequestDto;
import com.crmportal.response.dto.ExpenseItemResponseDto;
import com.crmportal.response.dto.ExpenseManagementResponseDto;
import com.crmportal.utility.DateMapper;

@Mapper(
		componentModel = "spring",
		uses = DateMapper.class,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface ExpenseItemMapper {

	@Mapping(source = "itemPurchaseDate", target = "itemPurchaseDate", qualifiedByName = "dateStringToDateTime", defaultExpression = "java(null)")
	ExpenseItemEntity requestToEntity(ExpenseItemRequestDto request);

	@Mapping(source = "itemPurchaseDate", target = "itemPurchaseDate", qualifiedByName = "dateStringToDateTime", defaultExpression = "java(null)")
	void updateEntityFromRequest(ExpenseItemRequestDto request, @MappingTarget ExpenseItemEntity entity);

	@Mapping(source = "itemPurchaseDate", target = "itemPurchaseDate", qualifiedByName = "dateTimeToString", defaultExpression = "java(null)")
	ExpenseItemResponseDto entityToResponse(ExpenseItemEntity entity);

}
