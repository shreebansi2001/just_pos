package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.request.dto.UserMasterRequestDto;
import com.crmportal.response.dto.UserMasterResponseDto;

@Mapper(
    componentModel = "spring",
    uses = { UserBasicDetailsMasterMapper.class, PlansMapper.class },
    		unmappedTargetPolicy = ReportingPolicy.IGNORE,
    	    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface UserMasterMapper {

    UserMasterResponseDto entityToResponse(UserMasterEntity entity);

    UserMasterEntity requestToEntity(@Valid UserMasterRequestDto request);

    void updateEntityFromRequest(@Valid UserMasterRequestDto request, @MappingTarget UserMasterEntity entity);

    UserMasterRequestDto entityToRequest(UserMasterEntity entity);
}
