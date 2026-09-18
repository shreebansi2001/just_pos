package com.crmportal.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.LeadStatusEntity;
import com.crmportal.response.dto.LeadStatusResponseDto;

@Mapper(
		 componentModel = "spring",
		 nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		 nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
)
public interface LeadStatusMapper {

	@Mapping(target = "createdAt", dateFormat = "dd/MM/yyyy HH:mm:ss")
	@Mapping(target = "updatedAt", dateFormat = "dd/MM/yyyy HH:mm:ss")
    @Mapping(target = "leadStatusTypeId", source = "leadStatus.leadStatusTypeId")
	LeadStatusResponseDto entityToResponse(LeadStatusEntity entity);
	
	List<LeadStatusResponseDto> entityToResponse(List<LeadStatusEntity> entity);
}
