package com.crmportal.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.AdminTemplateModuleEntity;
import com.crmportal.request.dto.AdminTemplateModuleRequestDTO;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface AdminTemplateModuleMapper {

	AdminTemplateModuleEntity requestToEntity(AdminTemplateModuleRequestDTO request);
	
	@Mapping(target = "createdAt", dateFormat = "dd-MM-yyyy")
	@Mapping(target = "templateModuleMaster.createdAt", dateFormat = "dd-MM-yyyy")
	@Mapping(target = "templateMaster.createdAt", dateFormat = "dd-MM-yyyy")
	@Mapping(target = "templateMaster.templateModuleMaster.createdAt", dateFormat = "dd-MM-yyyy")
	AdminTemplateModuleResponseDto entityToResponse(AdminTemplateModuleEntity entity);
	
	@Mapping(target = "createdAt", dateFormat = "dd-MM-yyyy")
	@Mapping(target = "templateModuleMaster.createdAt", dateFormat = "dd-MM-yyyy")
	@Mapping(target = "templateMaster.createdAt", dateFormat = "dd-MM-yyyy")
	@Mapping(target = "templateMaster.templateModuleMaster.createdAt", dateFormat = "dd-MM-yyyy")
	List<AdminTemplateModuleResponseDto> entityToResponse(List<AdminTemplateModuleEntity> entity);
}
