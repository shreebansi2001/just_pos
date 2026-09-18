package com.crmportal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.DecoreMainCategoryItemMasterRequestDto;
import com.crmportal.response.dto.DecoreMainCategoryItemMasterResponseDto;

@Service
public interface DecoreMainCategoryItemMasterService {

	DecoreMainCategoryItemMasterResponseDto saveDecoreItem(DecoreMainCategoryItemMasterRequestDto request);

	Page<DecoreMainCategoryItemMasterResponseDto> getAllDecoreItems(Long userId, String itemName, Long categoryId,
			Boolean isActive, Pageable pageable);

	DecoreMainCategoryItemMasterResponseDto getDecoreItemById(Long id);

	Boolean deleteDecoreItemById(Long id);

	boolean updateDecoreItemStatus(Long id, Boolean isActive);
}