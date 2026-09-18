package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.RawMaterialSupplierEntity;
import com.crmportal.request.dto.RawMaterialSupplierRequestDto;

@Mapper(
	    componentModel = "spring",
	    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
	    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
	)
public interface RawMaterialSupplierMapper {

	RawMaterialSupplierEntity requestToEntity(RawMaterialSupplierRequestDto rawMaterialSupplierRequestDto);

}
