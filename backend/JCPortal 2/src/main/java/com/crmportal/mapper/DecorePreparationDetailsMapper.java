package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.DecorePreparationDetailsEntity;
import com.crmportal.response.dto.DecorePreparationDetailsResponseDto;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface DecorePreparationDetailsMapper {

	DecorePreparationDetailsResponseDto entityToResponse(DecorePreparationDetailsEntity detail);

}
