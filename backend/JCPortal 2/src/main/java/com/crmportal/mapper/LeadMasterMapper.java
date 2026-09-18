package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.LeadMasterEntity;
import com.crmportal.request.dto.LeadMasterRequestDto;
import com.crmportal.request.dto.LeadMasterResponseDto;
import com.crmportal.utility.DateMapper;

@Mapper(componentModel = "spring", uses = {
		DateMapper.class }, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface LeadMasterMapper {

	//@Mapping(target = "leadFollowUpDate", source = "leadFollowUpDate", qualifiedByName = "stringToDateTime")
	@Mapping(source = "inquiryDate", target = "inquiryDate", qualifiedByName = "stringToDate")
	@Mapping(target = "closeDate", source = "closeDate", qualifiedByName = "stringToDate")

	LeadMasterEntity requestToEntity(@Valid LeadMasterRequestDto request);

	//@Mapping(target = "leadFollowUpDate", source = "leadFollowUpDate", qualifiedByName = "stringToDateTime")
	@Mapping(source = "inquiryDate", target = "inquiryDate", qualifiedByName = "stringToDate")
	@Mapping(target = "closeDate", source = "closeDate", qualifiedByName = "stringToDate")

	void updateEntityFromRequest(@Valid LeadMasterRequestDto request, @MappingTarget LeadMasterEntity entity);

	LeadMasterResponseDto entityToResponse(LeadMasterEntity entity);
}
