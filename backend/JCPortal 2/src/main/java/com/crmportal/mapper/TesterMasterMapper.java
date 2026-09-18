package com.crmportal.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.TesterMasterEntity;
import com.crmportal.request.dto.TesterMasterRequestDto;
import com.crmportal.response.dto.TesterMasterResponseDto;

@Mapper(
		componentModel = "spring",
		nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
)
public interface TesterMasterMapper {

	@Mapping(target = "birthDate", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "aniversaryDate", dateFormat = "dd/MM/yyyy")
	TesterMasterEntity requestToEntity(TesterMasterRequestDto request);

	TesterMasterEntity updateEntity(@MappingTarget TesterMasterEntity entity, TesterMasterRequestDto request);
	
	@Mapping(target = "birthDate", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "aniversaryDate", dateFormat = "dd/MM/yyyy")
	TesterMasterResponseDto entityToResponse(TesterMasterEntity entity);
	
	List<TesterMasterResponseDto> entityToResponse(List<TesterMasterEntity> entity);
}
