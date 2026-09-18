package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.UserAmcEntity;
import com.crmportal.response.dto.UserAmcResponseDto;

@Mapper(
		componentModel = "spring",
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface UserAmcMapper {

	UserAmcResponseDto entityToResponse(UserAmcEntity userAmcEntity);

}
