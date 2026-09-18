package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.PlansEntity;
import com.crmportal.request.dto.PlansRequestDto;
import com.crmportal.response.dto.PlansResponseDto;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PlansMapper {

    PlansEntity requestToEntity(@Valid PlansRequestDto request);

    PlansResponseDto entityToResponse(PlansEntity entity);
}
