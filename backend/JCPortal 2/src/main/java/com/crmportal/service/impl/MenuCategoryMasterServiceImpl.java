package com.crmportal.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.mapper.MenuCategoryMasterMapper;
import com.crmportal.repository.MenuCategoryMasterRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.MenuPreparationDetailsRepository;
import com.crmportal.repository.MenuPreparationRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.MenuCategoryMasterRequestDto;
import com.crmportal.response.dto.MenuCategoryForMenuReportResponseDto;
import com.crmportal.response.dto.MenuCategoryMasterResponseDto;
import com.crmportal.response.dto.MenuItemForReportResponseDto;
import com.crmportal.response.dto.MenuReportResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.DatabasePlanningService;
import com.crmportal.service.MenuCategoryMasterService;
import com.crmportal.service.MenuPreparationService;
import com.crmportal.service.UserFileService;

@Service
public class MenuCategoryMasterServiceImpl implements MenuCategoryMasterService {

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	MenuCategoryMasterRepository menuCategoryMasterRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	MenuCategoryMasterMapper menuCategoryMasterMapper;

	@Autowired
	Environment environment;

	@Autowired
	MenuPreparationRepository menuPreparationRepository;

	@Autowired
	DatabasePlanningService databasePlanningService;

	@Autowired
	UserFileService userFileService;

	@Autowired
	MenuItemMasterRepository itemMasterRepository;

	@Autowired
	MenuPreparationDetailsRepository menuPreparationDetailsRepository;

	@Override
	@Transactional
	public MenuCategoryMasterResponseDto addOrUpdateMenuCategory(@Valid MenuCategoryMasterRequestDto request, long id,
			MultipartFile file) {

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

		Optional<MenuCategoryMasterEntity> existing = menuCategoryMasterRepository
				.findByUserAndNameEnglishIgnoreCaseAndIsDeleteFalse(user, request.getNameEnglish());

		if (existing.isPresent() && (id == -1 || !existing.get().getId().equals(id))) {
			throw new RuntimeException(
					"Menu Category with name '" + request.getNameEnglish() + "' already exists for this user");
		}

		MenuCategoryMasterEntity entity;

		if (id == -1) {
			entity = menuCategoryMasterMapper.requestToEntity(request);
			entity.setUser(user);
			entity.setPrice(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO);

			if (request.getSequence() != null) {
				menuCategoryMasterRepository.shiftSequencesForUser(user.getId(), request.getSequence());
				entity.setSequence(request.getSequence());
			} else {
				Integer lastSeq = menuCategoryMasterRepository.findMaxSequenceByUserAndIsDeleteFalse(user.getId());
				entity.setSequence(lastSeq == null ? 1 : lastSeq + 1);
			}
			entity.setUuid(databasePlanningService.getOrCreateUserUuid(user));
		} else {
			entity = menuCategoryMasterRepository.findByIdAndUserAndIsDeleteFalse(id, user)
					.orElseThrow(() -> new RuntimeException("Menu Category not found with id: " + id));

			if (request.getSequence() != null) {
				Optional<MenuCategoryMasterEntity> conflict = menuCategoryMasterRepository
						.findByUserAndSequenceAndIsDeleteFalse(user, request.getSequence());
				if (conflict.isPresent() && !conflict.get().getId().equals(entity.getId())) {
					menuCategoryMasterRepository.shiftSequencesForUser(user.getId(), request.getSequence());
				}
				entity.setSequence(request.getSequence());
			}
			menuCategoryMasterMapper.updateEntityFromRequest(request, entity);
			entity.setPrice(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO);
			entity.setUpdatedAt(commonService.getCurrentDateTime());
		}

		MenuCategoryMasterEntity saved = menuCategoryMasterRepository.save(entity);
		if (file != null && !file.isEmpty()) {
			try {
				userFileService.storeFile(user.getId(), ModuleName.MENUCATEGORY.toString(), saved.getId(),
						FileType.IMAGE.toString(), file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return menuCategoryMasterMapper.entityToResponse(saved);
	}

	@Override
	public List<MenuCategoryMasterResponseDto> getAllMenuCategoryByUserId(Long userId, String menuCategoryName,
			Boolean isActive) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);
		if (!userOpt.isPresent()) {
			return Collections.emptyList();
		}

		UserMasterEntity user = userOpt.get();
		List<MenuCategoryMasterEntity> menuCategoryEntities;

		// Case: when menuCategoryName is empty/null
		if (menuCategoryName == null || menuCategoryName.trim().isEmpty()) {
			if (isActive == null) {
				menuCategoryEntities = menuCategoryMasterRepository
						.findAllByUserAndIsDeleteFalseOrderBySequenceAsc(user);
			} else {
				menuCategoryEntities = menuCategoryMasterRepository
						.findAllByUserAndIsDeleteFalseAndIsActiveOrderBySequenceAsc(user, isActive);
			}
		} else {
			if (isActive == null) {
				menuCategoryEntities = menuCategoryMasterRepository
						.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseOrderBySequenceAsc(
								menuCategoryName, user);
			} else {
				menuCategoryEntities = menuCategoryMasterRepository
						.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndIsActiveOrderBySequenceAsc(
								menuCategoryName, user, isActive);
			}
		}

		List<MenuCategoryMasterResponseDto> responseDtos = new ArrayList<>();
		for (MenuCategoryMasterEntity entity : menuCategoryEntities) {
			MenuCategoryMasterResponseDto responseDto = menuCategoryMasterMapper.entityToResponse(entity);

			if (entity.getImagePath() != null && !entity.getImagePath().isEmpty()) {
				responseDto.setImagePath(environment.getProperty("app.image.url") + entity.getImagePath());
			} else {
				responseDto.setImagePath("");
			}

			if (entity.getCreatedAt() != null) {
				responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
			}
			responseDto.setUserId(userId);
			responseDtos.add(responseDto);
		}

		return responseDtos;
	}

	@Override
	public MenuCategoryMasterResponseDto getMenuCategoryById(Long id) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		Optional<MenuCategoryMasterEntity> entityOptional = menuCategoryMasterRepository.findByIdAndIsDeleteFalse(id);

		if (!entityOptional.isPresent()) {
			return null;
		} else {
			MenuCategoryMasterEntity entity = entityOptional.get();
			System.err.println(entity.getId());
			MenuCategoryMasterResponseDto responseDto = menuCategoryMasterMapper.entityToResponse(entity);

			if (entity.getImagePath() != null && !entity.getImagePath().isEmpty()) {
				responseDto.setImagePath(environment.getProperty("app.image.url") + entity.getImagePath());
			} else {
				responseDto.setImagePath("");
			}
			responseDto.setUserId(entity.getUser().getId());
			if (entity.getCreatedAt() != null) {
				responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
			}

			return responseDto;
		}
	}

	@Override
	public Boolean deleteMenuCategoryById(Long id) {

		Optional<MenuCategoryMasterEntity> entityOptional = menuCategoryMasterRepository.findByIdAndIsDeleteFalse(id);

		if (!entityOptional.isPresent()) {
			throw new RuntimeException("Menu Category not found with id: " + id);
		} else {
			MenuCategoryMasterEntity entity = entityOptional.get();
			if (menuPreparationDetailsRepository.findAllByMenuCategory(entity).size() > 0) {
				throw new RuntimeException("Please Delete Menu Category in Menu Planning First");
			}
			if (itemMasterRepository.findAllByMenuCategoryAndIsDeleteFalse(entity).size() > 0) {

				throw new RuntimeException("Please Delete Menu Item First");
			}

			entity.setIsDelete(true);
			menuCategoryMasterRepository.save(entity);
			return true;
		}
	}

	@Override
	public boolean updateMenuCategoryStatus(Long id, Boolean isActive) {
		Optional<MenuCategoryMasterEntity> entityOpt = menuCategoryMasterRepository.findByIdAndIsDeleteFalse(id);
		if (!entityOpt.isPresent()) {
			throw new RuntimeException("Menu Category not found with id: " + id);
		}

		MenuCategoryMasterEntity entity = entityOpt.get();
		entity.setIsActive(isActive);
		menuCategoryMasterRepository.save(entity);
		return true;
	}

	@Override
	public List<MenuReportResponseDto> getMenuReportCategoryItem(Long menuPreparationId) {
		try {

			List<MenuCategoryForMenuReportResponseDto> categoryResponseDtos = menuCategoryMasterRepository
					.findByMenuPreparationId(menuPreparationId);
			List<MenuReportResponseDto> menuReportList = new ArrayList<>();

			for (MenuCategoryForMenuReportResponseDto categoryDto : categoryResponseDtos) {
				Long menuCategoryId = categoryDto.getId();
				MenuReportResponseDto menuReportResponseDto = new MenuReportResponseDto();
				menuReportResponseDto.setId(menuCategoryId);
				menuReportResponseDto.setNameEnglish(categoryDto.getNameEnglish());
				menuReportResponseDto.setNameHindi(categoryDto.getNameHindi());
				menuReportResponseDto.setNameGujarati(categoryDto.getNameGujarati());
				menuReportResponseDto
						.setImagePath(environment.getProperty("app.image.url") + categoryDto.getImagePath());
				menuReportResponseDto.setMenuNotes(categoryDto.getMenuNotes());
				menuReportResponseDto.setSlogan(categoryDto.getSlogan());

				List<MenuItemForReportResponseDto> itemsResponseDtos = menuPreparationRepository
						.findByMenuCategoryIdAndMenuPreparationId(menuCategoryId, menuPreparationId);
				menuReportResponseDto.setMenuItems(itemsResponseDtos);

				menuReportList.add(menuReportResponseDto);
			}

			return menuReportList;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get Menu Report Category And Items", e);
		}
	}
}
