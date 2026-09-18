package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.UserGodownEntity;
import com.crmportal.request.dto.UserGodownRequestDto;
import com.crmportal.response.dto.UserGodownResponseDto;

@Mapper(
		componentModel = "spring",
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface UserGodownMapper {

	UserGodownEntity requestToEntity(UserGodownRequestDto request);

	UserGodownEntity updateEntityFromRequest(@MappingTarget UserGodownEntity entity, UserGodownRequestDto request);

	UserGodownResponseDto entityToResponse(UserGodownEntity entity);

}
