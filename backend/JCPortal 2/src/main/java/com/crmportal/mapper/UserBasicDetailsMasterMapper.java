package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.request.dto.UserMasterRequestDto;
import com.crmportal.response.dto.UserBasicDetailsMasterResponseDto;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface UserBasicDetailsMasterMapper {

    UserBasicDetailsMasterEntity requestToEntity(@Valid UserMasterRequestDto request);

    UserBasicDetailsMasterResponseDto entityToResponse(UserBasicDetailsMasterEntity entity);
}
