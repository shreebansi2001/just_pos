package com.crmportal.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.CoupenEntity;
import com.crmportal.request.dto.CoupenMasterRequestDto;
import com.crmportal.response.dto.CoupenMasterResponseDto;
import com.crmportal.utility.DateMapper;

@Mapper(
		componentModel = "spring",
		uses = DateMapper.class,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface CoupenMasterMapper {

	@Mapping(source = "expireDate", target = "expireDate", qualifiedByName = "stringToDateTime")
	CoupenEntity requestToEntity(CoupenMasterRequestDto request);

	CoupenEntity updateEntityFromRequest(CoupenMasterRequestDto request, @MappingTarget CoupenEntity entityFromDb);

	CoupenMasterResponseDto entityToResponse(CoupenEntity entity);

}
