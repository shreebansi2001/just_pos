package com.crmportal.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.MenuPreparationDetailsEntity;
import com.crmportal.request.dto.MenuPreparationDetailsRequestDto;
import com.crmportal.response.dto.MenuPreparationDetailsResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface MenuPreparationDetailsMapper {

	MenuPreparationDetailsEntity requestToEntity(MenuPreparationDetailsRequestDto menuPreparationDetailsRequestDto);

	MenuPreparationDetailsResponseDto entityToResponse(MenuPreparationDetailsEntity menuPreparationDetailsEntity);

	List<MenuPreparationDetailsResponseDto> entityToResponse(List<MenuPreparationDetailsEntity> detailsResponseDtos);

}
