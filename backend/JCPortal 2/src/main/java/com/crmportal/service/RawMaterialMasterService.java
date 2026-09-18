package com.crmportal.service;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.crmportal.request.dto.RawMaterialMasterRequestDto;
import com.crmportal.request.dto.UpdateRawMaterialRequestDto;
import com.crmportal.response.dto.RawMaterialCategoryChangeResponseDto;
import com.crmportal.response.dto.RawMaterialMasterResponseDto;

@Service
public interface RawMaterialMasterService {

	RawMaterialMasterResponseDto addOrUpdateRawMaterial(@Valid RawMaterialMasterRequestDto request, long id);

	Map<String, Object> getAllRawMaterialByUserId(Long userid, Long rawMateriaCatlId,
			String rawMaterialName, Boolean isActive, Long unitid, Integer pageNo, Integer pageSize,Boolean isAsc, Boolean isPurchaseApprove,
			Long purchaseApproveId);

	RawMaterialMasterResponseDto getRawMaterialById(Long id);

	Boolean deleteRawMaterialById(Long id);

	boolean updateRawMaterialStatus(Long id, Boolean isActive);

	Boolean updateSequence(@Valid List<UpdateRawMaterialRequestDto> request);

	Map<String, Object> getAllRawMaterialByUserId(Long userid, String rawMaterialName, Boolean isActive);

	Map<String, Object> getAllGeneralFix(Long userId, String rawMaterialName, Boolean isGeneralFix, int pageIndex, int size);

	Boolean updateRawMaterialItemCategory(List<Long> rawMaterialIds, Long newCatId, Long userId);

	Page<RawMaterialCategoryChangeResponseDto> getRawMaterialItemByCategory(List<Long> catIds, Long userId, Pageable pageable);

	Boolean updateRawMaterialSupplier(List<Long> rawMaterialIds, Long newSupplierId, Long userId);

}
