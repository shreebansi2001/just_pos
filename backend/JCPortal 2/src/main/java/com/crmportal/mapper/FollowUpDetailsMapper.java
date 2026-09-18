package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.FollowUpDetailsEntity;
import com.crmportal.request.dto.FollowUpDetailsRequestDto;
import com.crmportal.response.dto.FollowUpDetailsResponseDto;
import com.crmportal.utility.DateMapper;

@Mapper(
		componentModel = "spring",
		uses = DateMapper.class,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface FollowUpDetailsMapper {

	@Mapping(source = "followUpDate", target = "followUpDate", qualifiedByName = "stringToDateTime", defaultExpression = "java(null)")
	FollowUpDetailsEntity requestToEntity(FollowUpDetailsRequestDto followUpDetailsRequestDto);

	@Mapping(source = "followUpDate", target = "followUpDate", qualifiedByName = "stringToDateTime", defaultExpression = "java(null)")
	void updateEntityFromRequest(FollowUpDetailsRequestDto followUpDetailsRequestDto,
			@MappingTarget  FollowUpDetailsEntity followUpDetailsEntity);

	@Mapping(source = "followUpDate", target = "followUpDate", qualifiedByName = "dateTimeToString", defaultExpression = "java(null)")
	@Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "dateTimeToString", defaultExpression = "java(null)")
	FollowUpDetailsResponseDto entityToResponse(FollowUpDetailsEntity followUpDetailsEntity);

}
