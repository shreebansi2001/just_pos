package com.crmportal.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.LeadSubSourceEntity;
import com.crmportal.response.dto.LeadSubSourceResponseDto;
import com.crmportal.response.dto.LeadSubSourceResponseDto2;

@Mapper(
		componentModel = "spring",
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
)
public interface LeadSubSourceMapper {

	@Mapping(target = "dateTime", dateFormat = "dd/MM/yyyy HH:mm:ss")
	@Mapping(target = "createdAt", dateFormat = "dd/MM/yyyy HH:mm:ss")
	@Mapping(target = "updatedAt", dateFormat = "dd/MM/yyyy HH:mm:ss")
	LeadSubSourceResponseDto entityToResponse(LeadSubSourceEntity entity);
	
	List<LeadSubSourceResponseDto> entityToResponse(List<LeadSubSourceEntity> entity);
	
	@Mapping(target = "dateTime", dateFormat = "dd/MM/yyyy HH:mm:ss")
	@Mapping(target = "createdAt", dateFormat = "dd/MM/yyyy HH:mm:ss")
	@Mapping(target = "updatedAt", dateFormat = "dd/MM/yyyy HH:mm:ss")
	LeadSubSourceResponseDto2 toResponse(LeadSubSourceEntity entity);
	
	List<LeadSubSourceResponseDto2> toResponse(List<LeadSubSourceEntity> entity);
	
}
