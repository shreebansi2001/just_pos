package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.MenuAllocationChangeRequestDto;
import com.crmportal.request.dto.MenuAllocationItemRequestDto;
import com.crmportal.request.dto.MenuItemMasterRequestDto;
import com.crmportal.request.dto.MenuRawMaterialIdsDto;
import com.crmportal.response.dto.ExistingItemRawResponseDto;
import com.crmportal.response.dto.MenuItemCategoryChangeResponseDto;
import com.crmportal.response.dto.MenuItemMasterResponseDto;

@Service
public interface MenuItemMasterService {

	MenuItemMasterResponseDto addOrUpdateMenuItemMaster(@Valid MenuItemMasterRequestDto request, long id,MultipartFile file);

	Page<MenuItemMasterResponseDto> getAllMenuItem(Long userId, String itemName, Pageable pageable, Long menuCatId, Long menuSubCatId, Boolean isAcs, Boolean isWithRecipe);

	MenuItemMasterResponseDto getMenuItemById(Long id);

	boolean updateMenuItemStatus(Long id, Boolean isActive);

	Boolean deleteMenuItemById(Long id);

	Boolean deleteMenuItemRawmaterialById(MenuRawMaterialIdsDto id);

	Boolean updateMenuItemCategory(List<Long> menuItemIds, Long newCatId, Long userId);
	
	Boolean updateMenuItemSubCategory(List<Long> menuItemIds, Long newMenuSubCatId,
			Long userId);

	List<MenuItemCategoryChangeResponseDto> getAllMenuItemsByCategory(List<Long> catIds, List<Long> subcatIds,
			Long userId, String type);

	Boolean updateMenuAllocation(String toType, List<MenuAllocationItemRequestDto> allocationItemRequestDtos,
			Long userId);

	Boolean updateMenuAllocationItemConfig(List<MenuAllocationChangeRequestDto> request);

	List<ExistingItemRawResponseDto> getAllExistingItems(Long userId, Boolean isCaptainRecipe);

}
