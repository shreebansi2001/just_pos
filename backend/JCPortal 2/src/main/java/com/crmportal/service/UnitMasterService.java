package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.UnitMasterRequestDto;
import com.crmportal.response.dto.UnitHierarchyDto;
import com.crmportal.response.dto.UnitMasterResponseDto;

@Service
public interface UnitMasterService {

	UnitMasterResponseDto addOrUpdateUnitMaster(@Valid UnitMasterRequestDto request, long parseLong);

	List<UnitMasterResponseDto> getAllByUserId(Long userid, String unitName, Boolean isActive);

	UnitMasterResponseDto getById(Long id);

	Boolean deleteById(Long id);

	Boolean updateStatusById(Long id, Boolean isActive);

	UnitHierarchyDto getParentUnitsWithChildren(Long parentId);

}
