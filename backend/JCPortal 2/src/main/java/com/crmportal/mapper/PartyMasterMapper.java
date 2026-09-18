package com.crmportal.mapper;

import javax.validation.Valid;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.request.dto.PartyMasterRequestDto;
import com.crmportal.response.dto.MenuItemPartyMasterResponseDto;
import com.crmportal.response.dto.PartyMasterResponseDto;
import com.crmportal.utility.DateMapper;
 
@Mapper(
		   componentModel = "spring",
		   uses = DateMapper.class,
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface PartyMasterMapper {

	PartyMasterResponseDto entityToResponse(PartyMasterEntity savedEntity);

	@Mapping(source = "opbDate", target = "opbDate", qualifiedByName = "stringToDate")
	PartyMasterEntity requestToEntity(@Valid PartyMasterRequestDto request);

	@Mapping(source = "opbDate", target = "opbDate", qualifiedByName = "stringToDate")
	void updateEntityFromRequest(@Valid PartyMasterRequestDto request, @MappingTarget  PartyMasterEntity entity);

	MenuItemPartyMasterResponseDto entityToResponse2(PartyMasterEntity party);

}
