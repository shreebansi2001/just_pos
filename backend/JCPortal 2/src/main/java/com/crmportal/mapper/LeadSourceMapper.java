package com.crmportal.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.LeadSourceEntity;
import com.crmportal.response.dto.LeadSourceResponseDto;

@Mapper(
		 componentModel = "spring",
		 nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		 nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
)
public interface LeadSourceMapper {

	@Mapping(target = "createdAt", dateFormat = "dd/MM/yyyy HH:mm:ss")
	@Mapping(target = "updatedAt", dateFormat = "dd/MM/yyyy HH:mm:ss")
	LeadSourceResponseDto entityToResponse(LeadSourceEntity entity);
	
	List<LeadSourceResponseDto> entityToResponse(List<LeadSourceEntity> entity);
}
