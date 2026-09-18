package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.KitchenAreaMasterEntity;
import com.crmportal.request.dto.KitchenAreaMasterRequestDto;
import com.crmportal.response.dto.KitchenAreaMasterResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface KitchenAreaMasterMapper {

	KitchenAreaMasterEntity requestToEntity(@Valid KitchenAreaMasterRequestDto request);

	void updateEntityFromRequest(@Valid KitchenAreaMasterRequestDto request,@MappingTarget KitchenAreaMasterEntity entity);

	KitchenAreaMasterResponseDto entityToResponse(KitchenAreaMasterEntity entity);

}
