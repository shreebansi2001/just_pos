package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.MenuCategoryMasterRequestDto;
import com.crmportal.response.dto.MenuCategoryMasterResponseDto;
import com.crmportal.response.dto.MenuReportResponseDto;

@Service
public interface MenuCategoryMasterService {

	MenuCategoryMasterResponseDto addOrUpdateMenuCategory(@Valid MenuCategoryMasterRequestDto request, long id,MultipartFile file);

	List<MenuCategoryMasterResponseDto> getAllMenuCategoryByUserId(Long userId, String menuCategoryName, Boolean isActive);

	MenuCategoryMasterResponseDto getMenuCategoryById(Long id);

	Boolean deleteMenuCategoryById(Long id);

	boolean updateMenuCategoryStatus(Long id, Boolean isActive);

	
	List<MenuReportResponseDto> getMenuReportCategoryItem(Long menuPreparationId);

}
