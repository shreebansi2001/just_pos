package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.crmportal.entity.EventFunctionQuotationItemEntity;
import com.crmportal.request.dto.EventFunctionQuotationItemsRequestDto;
import com.crmportal.utility.DateMapper;

@Mapper(
	    componentModel = "spring",
	    uses = { DateMapper.class },
	    		unmappedTargetPolicy = ReportingPolicy.IGNORE,
	    	    unmappedSourcePolicy = ReportingPolicy.IGNORE,
	    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
	    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
	)
	public interface EventFunctionQuotationItemMapper {

	    @Mapping(target = "functionDate", source = "functionDate",
	             qualifiedByName = "stringToFunctionInputDateTime")
	    EventFunctionQuotationItemEntity requestToEntity(EventFunctionQuotationItemsRequestDto dto);

	    @Mapping(target = "functionDate", source = "functionDate",
	             qualifiedByName = "stringToFunctionInputDateTime")
	    EventFunctionQuotationItemEntity updateEntityFromRequest(
	            EventFunctionQuotationItemsRequestDto dto,
	            @MappingTarget EventFunctionQuotationItemEntity entity);
	}
