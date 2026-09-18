package com.crmportal.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.CashAccountEntity;
import com.crmportal.request.dto.CashOpbRequestDto;
import com.crmportal.response.dto.CashOpbResponseDto;

@Mapper(
		componentModel = "spring",
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
)
public interface CashAccountMapper {

	CashAccountEntity requestToEntity(CashOpbRequestDto request);
	
	@Mapping(target = "createdAt", dateFormat = "dd/MM/yyyy HH:mm:ss")
	@Mapping(target = "updatedAt", dateFormat = "dd/MM/yyyy HH:mm:ss")
	CashOpbResponseDto entityToResponse(CashAccountEntity entity);
	
	List<CashOpbResponseDto> entityToResponse(List<CashAccountEntity> entity);
}
