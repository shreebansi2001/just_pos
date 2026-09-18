package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.DecoreMainCategoryMasterRequestDto;
import com.crmportal.response.dto.DecoreMainCategoryMasterResponseDto;

@Service
public interface DecoreMainCategoryMasterService {

	DecoreMainCategoryMasterResponseDto addOrUpdateDecoreMainCategory(@Valid DecoreMainCategoryMasterRequestDto request,
			long id,MultipartFile file);

	List<DecoreMainCategoryMasterResponseDto> getAllDecoreMainCategoryByUserId(Long userId, String categoryName,
			Boolean isActive);

	DecoreMainCategoryMasterResponseDto getDecoreMainCategoryById(Long id);

	Boolean deleteDecoreMainCategoryById(Long id);

	boolean updateDecoreMainCategoryStatus(Long id, Boolean isActive);
}