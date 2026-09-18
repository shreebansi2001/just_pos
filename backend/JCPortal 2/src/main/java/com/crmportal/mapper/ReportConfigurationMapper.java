package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.ReportConfigurationEntity;
import com.crmportal.request.dto.ReportConfigurationRequestDto;
import com.crmportal.response.dto.ReportConfigurationResponseDto;

@Mapper(
		componentModel = "spring",
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface ReportConfigurationMapper {

	ReportConfigurationEntity requestToEntity(ReportConfigurationRequestDto request);

	ReportConfigurationEntity updateEntityFromRequest(ReportConfigurationRequestDto request,
			@MappingTarget ReportConfigurationEntity entity);

	ReportConfigurationResponseDto entityToResponse(ReportConfigurationEntity entity);

}
