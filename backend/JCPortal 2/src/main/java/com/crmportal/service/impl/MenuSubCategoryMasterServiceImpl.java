package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.MenuSubCategoryMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.MenuCategoryMasterMapper;
import com.crmportal.mapper.MenuSubCategoryMasterMapper;
import com.crmportal.repository.MenuCategoryMasterRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.MenuSubCategoryMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.MenuSubCategoryMasterRequestDto;
import com.crmportal.response.dto.MenuSubCategoryMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.MenuSubCategoryMasterService;

@Service
public class MenuSubCategoryMasterServiceImpl implements MenuSubCategoryMasterService {

    private final MenuItemMasterRepository menuItemMasterRepository;

	@Autowired
	MenuSubCategoryMasterRepository menuSubCategoryMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	MenuSubCategoryMasterMapper menuSubCategoryMasterMapper;

	@Autowired
	CommonService commonService;

	@Autowired
	MenuCategoryMasterRepository categoryMasterRepository;

	@Autowired
	MenuCategoryMasterMapper categoryMasterMapper;

    MenuSubCategoryMasterServiceImpl(MenuItemMasterRepository menuItemMasterRepository) {
        this.menuItemMasterRepository = menuItemMasterRepository;
    }

	@Override
	@Transactional
	public MenuSubCategoryMasterResponseDto addOrUpdateMenuSubCategoryMaster(
			@Valid MenuSubCategoryMasterRequestDto request, long id) {

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

		Optional<MenuSubCategoryMasterEntity> existing = menuSubCategoryMasterRepository
				.findByUserAndNameEnglishIgnoreCaseAndIsDeleteFalse(user, request.getNameEnglish());

		if (existing.isPresent() && (id == -1 || !existing.get().getId().equals(id))) {
			throw new RuntimeException(
					"Sub-Category with name '" + request.getNameEnglish() + "' already exists for this user");
		}

		MenuCategoryMasterEntity menuCat = null;
		if (request.getMenuCatId() != null) {
			menuCat = categoryMasterRepository.findByIdAndIsDeleteFalse(request.getMenuCatId()).orElseThrow(
					() -> new RuntimeException("Menu Category not found with id: " + request.getMenuCatId()));
		}
		MenuSubCategoryMasterEntity entity;

		if (id == -1) {
			entity = menuSubCategoryMasterMapper.requestToEntity(request);
			entity.setUser(user);

		} else {
			entity = menuSubCategoryMasterRepository.findByIdAndUserAndIsDeleteFalse(id, user)
					.orElseThrow(() -> new RuntimeException("Sub-Category not found with id: " + id));

			menuSubCategoryMasterMapper.updateEntityFromRequest(request, entity);
			entity.setUpdatedAt(commonService.getCurrentDateTime());
		}
		entity.setMenuCategory(menuCat);
		MenuSubCategoryMasterEntity saved = menuSubCategoryMasterRepository.save(entity);
		return menuSubCategoryMasterMapper.entityToResponse(saved);
	}

	@Override
	public List<MenuSubCategoryMasterResponseDto> getAllMenuSubCategoryByUserId(Long userId, String menuSubCategoryName,
			Boolean isActive, Long menuCategoryId) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId).orElse(null);

		if (user == null) {
			return Collections.emptyList();
		}

		MenuCategoryMasterEntity menuCat = null;
		if (menuCategoryId != null) {
			menuCat = categoryMasterRepository.findByIdAndUserAndIsDeleteFalse(menuCategoryId, user).orElse(null);

			if (menuCat == null) {
				return Collections.emptyList();
			}
		}

		List<MenuSubCategoryMasterEntity> entities;

		boolean hasNameFilter = (menuSubCategoryName != null && !menuSubCategoryName.trim().isEmpty());
		boolean hasCategoryFilter = (menuCat != null);
		boolean hasActiveFilter = (isActive != null);

		if (!hasNameFilter) {
			if (!hasActiveFilter) {
				entities = !hasCategoryFilter ? menuSubCategoryMasterRepository.findAllByUserAndIsDeleteFalse(user)
						: menuSubCategoryMasterRepository.findAllByUserAndMenuCategoryAndIsDeleteFalse(user, menuCat);
			} else {
				entities = !hasCategoryFilter
						? menuSubCategoryMasterRepository.findAllByUserAndIsDeleteFalseAndIsActive(user, isActive)
						: menuSubCategoryMasterRepository.findAllByUserAndMenuCategoryAndIsDeleteFalseAndIsActive(user,
								menuCat, isActive);
			}
		} else {
			if (!hasActiveFilter) {
				entities = !hasCategoryFilter
						? menuSubCategoryMasterRepository
								.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(menuSubCategoryName, user)
						: menuSubCategoryMasterRepository
								.findByNameEnglishContainingIgnoreCaseAndUserAndMenuCategoryAndIsDeleteFalse(
										menuSubCategoryName, user, menuCat);
			} else {
				entities = !hasCategoryFilter
						? menuSubCategoryMasterRepository
								.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndIsActive(
										menuSubCategoryName, user, isActive)
						: menuSubCategoryMasterRepository
								.findByNameEnglishContainingIgnoreCaseAndUserAndMenuCategoryAndIsDeleteFalseAndIsActive(
										menuSubCategoryName, user, menuCat, isActive);
			}
		}

		List<MenuSubCategoryMasterResponseDto> responseDtos = new ArrayList<>();

		for (MenuSubCategoryMasterEntity entity : entities) {

			MenuSubCategoryMasterResponseDto dto = menuSubCategoryMasterMapper.entityToResponse(entity);

			if (entity.getCreatedAt() != null) {
				dto.setCreatedAt(entity.getCreatedAt().format(formatter));
			}

			dto.setUserId(userId);

			// FIX: use entity.getMenuCategory(), not menuCat
			if (entity.getMenuCategory() != null) {
				dto.setMenuCategory(categoryMasterMapper.entityToResponse(entity.getMenuCategory()));
			} else {
				dto.setMenuCategory(null);
			}

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	@Override
	public MenuSubCategoryMasterResponseDto getMenuSubCategoryById(Long id) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		MenuSubCategoryMasterEntity entity = menuSubCategoryMasterRepository.findByIdAndIsDeleteFalse(id).orElse(null);

		if (entity == null) {
			return null;
		}

		MenuSubCategoryMasterResponseDto dto = menuSubCategoryMasterMapper.entityToResponse(entity);

		if (entity.getCreatedAt() != null) {
			dto.setCreatedAt(entity.getCreatedAt().format(formatter));
		}

		dto.setUserId(entity.getUser().getId());

		if (entity.getMenuCategory() != null) {
			dto.setMenuCategory(categoryMasterMapper.entityToResponse(entity.getMenuCategory()));
		} else {
			dto.setMenuCategory(null);
		}

		return dto;
	}

	@Override
	public boolean deleteMenuSubCategoryById(Long id) {
		Optional<MenuSubCategoryMasterEntity> entityOpt = menuSubCategoryMasterRepository.findByIdAndIsDeleteFalse(id);
		if (!entityOpt.isPresent()) {
			throw new RuntimeException("Menu Sub Category not found with id: " + id);
		}
		MenuSubCategoryMasterEntity entity = entityOpt.get();
		entity.setIsDelete(true);
		menuSubCategoryMasterRepository.save(entity);
		return true;
	}

	@Override
	public boolean updateMenuSubCategoryStatus(Long id, Boolean isActive) {
		Optional<MenuSubCategoryMasterEntity> entityOpt = menuSubCategoryMasterRepository.findByIdAndIsDeleteFalse(id);
		if (!entityOpt.isPresent()) {
			throw new RuntimeException("Menu Sub Category not found with id: " + id);
		}
		MenuSubCategoryMasterEntity entity = entityOpt.get();
		entity.setIsActive(isActive);
		menuSubCategoryMasterRepository.save(entity);
		return true;
	}

}
