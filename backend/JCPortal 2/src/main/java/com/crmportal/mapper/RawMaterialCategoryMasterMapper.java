package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.RawMaterialCategoryMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.request.dto.RawMaterialCategoryMasterRequestDto;
import com.crmportal.response.dto.RawMaterialCategoryMasterResponseDto;

@Mapper(
	    componentModel = "spring",
	    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
	    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
	)
public interface RawMaterialCategoryMasterMapper {

	RawMaterialCategoryMasterEntity requestToEntity(@Valid RawMaterialCategoryMasterRequestDto request);

	RawMaterialCategoryMasterEntity updateEntityFromRequest(@Valid RawMaterialCategoryMasterRequestDto request,
		@MappingTarget RawMaterialCategoryMasterEntity entity);

	RawMaterialCategoryMasterResponseDto entityToResponse(RawMaterialCategoryMasterEntity entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "uuid", expression = "java(newUuid)")
	@Mapping(target = "isPublished", constant = "false")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "user", expression = "java(userMasterEntity)")
	@Mapping(target = "rawMaterialCatType", ignore = true)
	RawMaterialCategoryMasterEntity cloneForNew(RawMaterialCategoryMasterEntity source, @Context String newUuid,
			@Context UserMasterEntity userMasterEntity);

}
