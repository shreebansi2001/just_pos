package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.crmportal.entity.RoleMasterEntity;
import com.crmportal.request.dto.RoleMasterRequestDto;
import com.crmportal.response.dto.RoleMasterResponseDto;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface RoleMasterMapper {

    RoleMasterEntity requestToEntity(@Valid RoleMasterRequestDto request);
    
    RoleMasterResponseDto entityToResponse(RoleMasterEntity entity);
}
