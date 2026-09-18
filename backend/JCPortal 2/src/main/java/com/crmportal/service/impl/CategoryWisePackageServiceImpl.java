package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.CategoryWisePackageEntity;
import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.enums.CategoryWisePackageType;
import com.crmportal.repository.CategoryWisePackageRepository;
import com.crmportal.repository.MenuCategoryMasterRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.response.dto.CategoryWisePackageDto;
import com.crmportal.response.dto.CategoryWiseTypeCatResponseDto;
import com.crmportal.response.dto.CategoryWiseTypeItemResponseDto;
import com.crmportal.service.CategoryWisePackageService;

@Service
public class CategoryWisePackageServiceImpl implements CategoryWisePackageService {

	@Autowired
	MenuCategoryMasterRepository menuCategoryMasterRepository;
	
	@Autowired
	MenuItemMasterRepository menuItemMasterRepository;

	@Autowired
	CategoryWisePackageRepository categoryWisePackageRepository;
	
	@Override
	@Transactional
	public CategoryWisePackageDto addOrUpdateCategoryWisePackage(CategoryWisePackageDto request) {

	    categoryWisePackageRepository.deleteAllByUserId(request.getUserId());

	    if (request.getBasicPackage() != null && !request.getBasicPackage().isEmpty()) {

	        for (CategoryWiseTypeCatResponseDto categoryDto : request.getBasicPackage()) {

	            MenuCategoryMasterEntity category = menuCategoryMasterRepository
	                    .findById(categoryDto.getMenuCategoryId())
	                    .orElseThrow(() -> new RuntimeException("Menu category not found"));

	            categoryDto.setMenuCategoryName(category.getNameEnglish());

	            if (categoryDto.getItems() != null && !categoryDto.getItems().isEmpty()) {

	                for (CategoryWiseTypeItemResponseDto itemDto : categoryDto.getItems()) {

	                    MenuItemMasterEntity item = menuItemMasterRepository
	                            .findById(itemDto.getMenuItemId())
	                            .orElseThrow(() -> new RuntimeException("Menu item not found"));

	                    CategoryWisePackageEntity entity = new CategoryWisePackageEntity();
	                    entity.setUserId(request.getUserId());
	                    entity.setMenuCategory(category);
	                    entity.setMenuItem(item);
	                    entity.setType(CategoryWisePackageType.BASIC);

	                    categoryWisePackageRepository.save(entity);

	                    itemDto.setMenuItemName(item.getNameEnglish());
	                }
	            }
	        }
	    }

	    if (request.getPremiumPackage() != null && !request.getPremiumPackage().isEmpty()) {

	        for (CategoryWiseTypeCatResponseDto categoryDto : request.getPremiumPackage()) {

	            MenuCategoryMasterEntity category = menuCategoryMasterRepository
	                    .findById(categoryDto.getMenuCategoryId())
	                    .orElseThrow(() -> new RuntimeException("Menu category not found"));

	            categoryDto.setMenuCategoryName(category.getNameEnglish());

	            if (categoryDto.getItems() != null && !categoryDto.getItems().isEmpty()) {

	                for (CategoryWiseTypeItemResponseDto itemDto : categoryDto.getItems()) {

	                    MenuItemMasterEntity item = menuItemMasterRepository
	                            .findById(itemDto.getMenuItemId())
	                            .orElseThrow(() -> new RuntimeException("Menu item not found"));

	                    CategoryWisePackageEntity entity = new CategoryWisePackageEntity();
	                    entity.setUserId(request.getUserId());
	                    entity.setMenuCategory(category);
	                    entity.setMenuItem(item);
	                    entity.setType(CategoryWisePackageType.PREMIUM);

	                    categoryWisePackageRepository.save(entity);

	                    itemDto.setMenuItemName(item.getNameEnglish());
	                }
	            }
	        }
	    }

	    return request;
	}
	
	@Override
	public CategoryWisePackageDto getAllCategoryWisePackage(Long userId) {

		List<CategoryWisePackageEntity> entities = categoryWisePackageRepository.findAllByUserId(userId);

		Map<Long, CategoryWiseTypeCatResponseDto> basicMap = new LinkedHashMap<>();
		Map<Long, CategoryWiseTypeCatResponseDto> premiumMap = new LinkedHashMap<>();

		for (CategoryWisePackageEntity entity : entities) {

			Long categoryId = entity.getMenuCategory().getId();

			CategoryWiseTypeItemResponseDto itemDto = new CategoryWiseTypeItemResponseDto();
			itemDto.setMenuItemId(entity.getMenuItem().getId());
			itemDto.setMenuItemName(entity.getMenuItem().getNameEnglish());

			if (entity.getType() == CategoryWisePackageType.BASIC) {
				CategoryWiseTypeCatResponseDto categoryDto = basicMap.get(categoryId);

				if (categoryDto == null) {
					categoryDto = new CategoryWiseTypeCatResponseDto();
					categoryDto.setMenuCategoryId(categoryId);
					categoryDto.setMenuCategoryName(entity.getMenuCategory().getNameEnglish());
					categoryDto.setItems(new ArrayList<>());

					basicMap.put(categoryId, categoryDto);
				}

				categoryDto.getItems().add(itemDto);
			}

			else if (entity.getType() == CategoryWisePackageType.PREMIUM) {
				CategoryWiseTypeCatResponseDto categoryDto = premiumMap.get(categoryId);

				if (categoryDto == null) {
					categoryDto = new CategoryWiseTypeCatResponseDto();
					categoryDto.setMenuCategoryId(categoryId);
					categoryDto.setMenuCategoryName(entity.getMenuCategory().getNameEnglish());
					categoryDto.setItems(new ArrayList<>());

					premiumMap.put(categoryId, categoryDto);
				}

				categoryDto.getItems().add(itemDto);
			}
		}

		CategoryWisePackageDto response = new CategoryWisePackageDto();
		response.setUserId(userId);
		response.setBasicPackage(new ArrayList<>(basicMap.values()));
		response.setPremiumPackage(new ArrayList<>(premiumMap.values()));

		return response;
	}
	
	@Override
	public List<CategoryWiseTypeItemResponseDto> getCategoryWisePackageItemByType(Long userId, Long menuCategoryId,
			CategoryWisePackageType type) {

		List<CategoryWisePackageEntity> entities = categoryWisePackageRepository
				.findAllByUserIdAndMenuCategory_IdAndType(userId, menuCategoryId, type);

		List<CategoryWiseTypeItemResponseDto> response = new ArrayList<>();

		for (CategoryWisePackageEntity entity : entities) {

			CategoryWiseTypeItemResponseDto dto = new CategoryWiseTypeItemResponseDto();

			dto.setMenuItemId(entity.getMenuItem().getId());
			dto.setMenuItemName(entity.getMenuItem().getNameEnglish());
			dto.setInstruction(entity.getMenuItem().getInstructionEnglish());

			response.add(dto);
		}

		return response;
	}
}
