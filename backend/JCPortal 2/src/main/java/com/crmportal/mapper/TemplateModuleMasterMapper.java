package com.crmportal.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.TemplateModuleMasterEntity;
import com.crmportal.request.dto.TemplateModuleMasterRequestDto;
import com.crmportal.response.dto.TemplateModuleMasterResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface TemplateModuleMasterMapper {

	TemplateModuleMasterEntity requestToEntity(TemplateModuleMasterRequestDto request);
	
	@Mapping(target = "createdAt", dateFormat = "dd-MM-yyyy")
	TemplateModuleMasterResponseDto entityToResponse(TemplateModuleMasterEntity entity);
	
	@Mapping(target = "createdAt", dateFormat = "dd-MM-yyyy")
	List<TemplateModuleMasterResponseDto> entityToResponse(List<TemplateModuleMasterEntity> entities);
}
