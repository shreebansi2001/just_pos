package com.crmportal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.PlanFeatureEntity;
import com.crmportal.request.dto.PlanFeatureRequestDto;
import com.crmportal.response.dto.PlanFeatureResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface PlansFeatureMapper {

	PlanFeatureEntity RequestToEntity(PlanFeatureRequestDto featureRequest);

	PlanFeatureResponseDto entityToResponse(PlanFeatureEntity feature);

}
