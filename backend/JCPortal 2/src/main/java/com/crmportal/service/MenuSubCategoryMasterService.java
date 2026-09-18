package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.MenuSubCategoryMasterRequestDto;
import com.crmportal.response.dto.MenuSubCategoryMasterResponseDto;

@Service
public interface MenuSubCategoryMasterService {

	MenuSubCategoryMasterResponseDto addOrUpdateMenuSubCategoryMaster(@Valid MenuSubCategoryMasterRequestDto request,
			long id);

	List<MenuSubCategoryMasterResponseDto> getAllMenuSubCategoryByUserId(Long userId, String menuSubCategoryName,
			Boolean isActive,Long menuCategoryId);

	MenuSubCategoryMasterResponseDto getMenuSubCategoryById(Long id);

	boolean deleteMenuSubCategoryById(Long id);

	boolean updateMenuSubCategoryStatus(Long id, Boolean isActive);

}
