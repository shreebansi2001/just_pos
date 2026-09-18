package com.crmportal.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.request.dto.EventFunctionMasterRequestDto;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;
import com.crmportal.utility.DateMapper;

@Mapper(
		componentModel = "spring", 
		uses = DateMapper.class, 
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL, 
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface EventFunctionMasterMapper {

	@Mapping(target = "functionStartDateTime", source = "functionStartDateTime", qualifiedByName = "stringToDateTime")
	@Mapping(target = "functionEndDateTime", source = "functionEndDateTime", qualifiedByName = "stringToDateTime")
	EventFunctionMasterEntity requestToEntity(EventFunctionMasterRequestDto request);

	@Mapping(target = "functionStartDateTime", source = "functionStartDateTime", qualifiedByName = "stringToDateTime")
	@Mapping(target = "functionEndDateTime", source = "functionEndDateTime", qualifiedByName = "stringToDateTime")
	EventFunctionMasterEntity updateEntityFromRequest(EventFunctionMasterRequestDto request,
			@MappingTarget EventFunctionMasterEntity entity);

	@Mapping(target = "functionStartDateTime", source = "functionStartDateTime", qualifiedByName = "dateTimeToString")
	@Mapping(target = "functionEndDateTime", source = "functionEndDateTime", qualifiedByName = "dateTimeToString")
	EventFunctionMasterResponseDto entityToResponse(EventFunctionMasterEntity entity);
	
	@AfterMapping
	default void setCustomPackage(EventFunctionMasterEntity entity,
	        @MappingTarget EventFunctionMasterResponseDto dto) {
	    // handled manually in service
	    if (entity.getCustomPackage() != null) {
	        dto.setCustomPackageId(entity.getCustomPackage().getId());
	        dto.setCustomPackageName(entity.getCustomPackage().getNameEnglish());
	    } else {
	        dto.setCustomPackageId(null);
	        dto.setCustomPackageName(null);
	    }
	}
}
