package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.enums.CategoryWisePackageType;
import com.crmportal.response.dto.CategoryWisePackageDto;
import com.crmportal.response.dto.CategoryWiseTypeItemResponseDto;

@Service
public interface CategoryWisePackageService {

	CategoryWisePackageDto addOrUpdateCategoryWisePackage(CategoryWisePackageDto request);

	CategoryWisePackageDto getAllCategoryWisePackage(Long userId);

	List<CategoryWiseTypeItemResponseDto> getCategoryWisePackageItemByType(Long userId, Long menuCategoryId,
			CategoryWisePackageType type);

}
