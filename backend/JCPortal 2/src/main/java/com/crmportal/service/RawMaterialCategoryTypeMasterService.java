package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.RawMaterialCategoryTypeMasterRequestDto;
import com.crmportal.response.dto.RawMaterialCategoryTypeMasterResponseDto;

@Service
public interface RawMaterialCategoryTypeMasterService {

	RawMaterialCategoryTypeMasterResponseDto addOrUpdateRawMaterialCategoryType(
			@Valid RawMaterialCategoryTypeMasterRequestDto request, long id);

	List<RawMaterialCategoryTypeMasterResponseDto> getAllRawMaterialCategoryTypeByUserId(Long userId,
			String categoryTypeName, Boolean isActive);

	RawMaterialCategoryTypeMasterResponseDto getRawMaterialCategoryTypeById(Long id);

	Boolean deleteRawMaterialCategoryTypeById(Long id);

	boolean updateRawMaterialCategoryTypeStatus(Long id, Boolean isActive);

}
