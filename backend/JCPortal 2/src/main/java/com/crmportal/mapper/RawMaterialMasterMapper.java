package com.crmportal.mapper;

import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.RawMaterialSupplierEntity;
import com.crmportal.request.dto.RawMaterialMasterRequestDto;
import com.crmportal.response.dto.RawMaterialInMenuItemResponseDto;
import com.crmportal.response.dto.RawMaterialMasterResponseDto;
import com.crmportal.response.dto.RawMaterialSupplierResponseDto;
import com.crmportal.utility.DateMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
		   componentModel = "spring",
		   uses = DateMapper.class,
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface RawMaterialMasterMapper {

	@Mapping(target = "file", ignore = true)
	@Mapping(source = "expiryDate", target = "expiryDate", qualifiedByName = "stringToDate")
    RawMaterialMasterEntity requestToEntity(RawMaterialMasterRequestDto request);

	@Mapping(target = "file", ignore = true)
	@Mapping(source = "expiryDate", target = "expiryDate", qualifiedByName = "stringToDate")
    RawMaterialMasterEntity updateEntityFromRequest(RawMaterialMasterRequestDto request, @MappingTarget RawMaterialMasterEntity entity);

    RawMaterialMasterResponseDto entityToResponse(RawMaterialMasterEntity entity);

    List<RawMaterialMasterResponseDto> entityListToResponseList(List<RawMaterialMasterEntity> entities);

    RawMaterialSupplierResponseDto rawMaterialSupplierEntityToRawMaterialSupplierResponseDto(RawMaterialSupplierEntity entity);

    List<RawMaterialSupplierResponseDto> rawMaterialSupplierEntityListToRawMaterialSupplierResponseDtoList(
            List<RawMaterialSupplierEntity> entities
    );

	RawMaterialInMenuItemResponseDto entityToResponse2(RawMaterialMasterEntity entity);
}
