package com.crmportal.mapper;

import java.util.ArrayList;
import java.util.Collections;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.request.dto.EventMasterRequestDto;
import com.crmportal.response.dto.EventMasterResponseDto;
import com.crmportal.utility.DateMapper;

@Mapper(
    componentModel = "spring",
    		uses = DateMapper.class,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface EventMasterMapper {
	
	@Mapping(source = "inquiryDate", target = "inquiryDate", qualifiedByName = "stringToDate")
    @Mapping(source = "eventStartDateTime", target = "eventStartDateTime", qualifiedByName = "stringToDateTime")
    @Mapping(source = "eventEndDateTime", target = "eventEndDateTime", qualifiedByName = "stringToDateTime")
    @Mapping(source = "groomBirthDate", target = "groomBirthDate", qualifiedByName = "stringToDate", defaultExpression = "java(null)")
    @Mapping(source = "brideBirthDate", target = "brideBirthDate", qualifiedByName = "stringToDate", defaultExpression = "java(null)")
    EventMasterEntity requestToEntity(EventMasterRequestDto request);

	@Mapping(source = "inquiryDate", target = "inquiryDate", qualifiedByName = "stringToDate")
    @Mapping(source = "eventStartDateTime", target = "eventStartDateTime", qualifiedByName = "stringToDateTime")
    @Mapping(source = "eventEndDateTime", target = "eventEndDateTime", qualifiedByName = "stringToDateTime")
    @Mapping(source = "groomBirthDate", target = "groomBirthDate", qualifiedByName = "stringToDate", defaultExpression = "java(null)")
    @Mapping(source = "brideBirthDate", target = "brideBirthDate", qualifiedByName = "stringToDate", defaultExpression = "java(null)")
	EventMasterEntity updateEntityFromRequest(EventMasterRequestDto request, @MappingTarget EventMasterEntity entity);

	EventMasterResponseDto entityToResponse(EventMasterEntity entity);
	
	@AfterMapping
	default void setBanquetHall(EventMasterEntity entity,
	        @MappingTarget EventMasterResponseDto dto) {

		if (entity.getBanquetHall() != null) {
	        dto.setBanquetHallId(entity.getBanquetHall().getId());
	        dto.setBanquetHallName(entity.getBanquetHall().getHallName());
	    } else {
	        dto.setBanquetHallId(null);
	        dto.setBanquetHallName("ODC");
	    }
	}
	
	
	@AfterMapping
	default void setCustomPackage(EventMasterEntity entity,
	        @MappingTarget EventMasterResponseDto dto) {
	    // handled manually in service — skip mapper auto-mapping
	    // banquet hall is also set here already
	    if (entity.getBanquetHall() != null) {
	        dto.setBanquetHallId(entity.getBanquetHall().getId());
	        dto.setBanquetHallName(entity.getBanquetHall().getHallName());
	    } else {
	        dto.setBanquetHallId(null);
	        dto.setBanquetHallName("ODC");
	    }
	}
}


