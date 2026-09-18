package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.RawMaterialCategoryMasterRequestDto;
import com.crmportal.response.dto.RawMaterialCategoryMasterResponseDto;

@Service
public interface RawMaterialCategoryMasterService {

	RawMaterialCategoryMasterResponseDto addOrUpdateRawMaterialCategory(
			@Valid RawMaterialCategoryMasterRequestDto request, Long valueOf);

	List<RawMaterialCategoryMasterResponseDto> getAllRawMaterialCategoryByUserId(Long userId, Long categoryTypeId, String categoryName,
			Boolean isActive);

	RawMaterialCategoryMasterResponseDto getRawMaterialCategoryById(Long id);

	Boolean deleteRawMaterialCategoryById(Long id);

	boolean updateRawMaterialCategoryStatus(Long id, Boolean isActive);

	List<RawMaterialCategoryMasterResponseDto> getRawmaterialCategoryByEventId(Long eventId);

	List<RawMaterialCategoryMasterResponseDto> getRawmaterialCategoryByTypeId(Long rawMaterialCategoryTypeId, Long userId);

}
