package com.crmportal.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.crmportal.controller.AdminTemplateModuleController;
import com.crmportal.entity.CaptainReceipeMasterEntity;
import com.crmportal.entity.ContactCategoryMasterEntity;
import com.crmportal.entity.DatabasePlanningEntity;
import com.crmportal.entity.KitchenAreaMasterEntity;
import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.MenuItemAllocationConfigEntity;
import com.crmportal.entity.MenuItemCaptainReceipeEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.MenuItemRawMaterialEntity;
import com.crmportal.entity.MenuItemRawMaterialRateDishCostingEntity;
import com.crmportal.entity.MenuSubCategoryMasterEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.PlansEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.mapper.ContactCategoryMasterMapper;
import com.crmportal.mapper.MenuCategoryMasterMapper;
import com.crmportal.mapper.MenuItemAllocationConfigMapper;
import com.crmportal.mapper.MenuItemMasterMapper;
import com.crmportal.mapper.MenuItemRawMaterialMapper;
import com.crmportal.mapper.MenuSubCategoryMasterMapper;
import com.crmportal.mapper.PartyMasterMapper;
import com.crmportal.mapper.UnitMasterMapper;
import com.crmportal.repository.CaptainReceipeMasterRepository;
import com.crmportal.repository.ContactCategoryMasterRepository;
import com.crmportal.repository.DatabasePlanningEntityRepository;
import com.crmportal.repository.KitchenAreaMasterRepository;
import com.crmportal.repository.MenuCategoryMasterRepository;
import com.crmportal.repository.MenuItemAllocationConfigRepository;
import com.crmportal.repository.MenuItemCaptainReceipeRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.MenuItemRawMaterialRateDishCostingRepository;
import com.crmportal.repository.MenuItemRawMaterialRepository;
import com.crmportal.repository.MenuPreparationDetailsRepository;
import com.crmportal.repository.MenuPreparationRepository;
import com.crmportal.repository.MenuSubCategoryMasterRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.ChefLabourItemRequestDto;
import com.crmportal.request.dto.InsideItemRequestDto;
import com.crmportal.request.dto.MenuAllocationChangeRequestDto;
import com.crmportal.request.dto.MenuAllocationItemRequestDto;
import com.crmportal.request.dto.MenuItemAllocationConfigRequestDto;
import com.crmportal.request.dto.MenuItemMasterRequestDto;
import com.crmportal.request.dto.MenuItemRawMaterialRequestDto;
import com.crmportal.request.dto.MenuRawMaterialIdsDto;
import com.crmportal.request.dto.OutsideItemRequestDto;
import com.crmportal.response.dto.CaptainReceipeMasterResponseDto;
import com.crmportal.response.dto.ChefLabourItemResponseDto;
import com.crmportal.response.dto.ExistingItemRawResponseDto;
import com.crmportal.response.dto.InsideItemResponseDto;
import com.crmportal.response.dto.MenuCategoryMasterResponseDto;
import com.crmportal.response.dto.MenuItemAllocationConfigResponseDto;
import com.crmportal.response.dto.MenuItemCaptainReceipeRequestDto;
import com.crmportal.response.dto.MenuItemCaptainReceipeResponseDto;
import com.crmportal.response.dto.MenuItemCategoryChangeResponseDto;
import com.crmportal.response.dto.MenuItemMasterResponseDto;
import com.crmportal.response.dto.MenuItemRawMaterialsResponseDto;
import com.crmportal.response.dto.OutsideItemResponseDto;
import com.crmportal.response.dto.PartyMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.DatabasePlanningService;
import com.crmportal.service.MenuItemMasterService;
import com.crmportal.service.UnitMasterService;
import com.crmportal.service.UserFileService;

@Service
public class MenuItemMasterServiceImpl implements MenuItemMasterService {

	private final AdminTemplateModuleController adminTemplateModuleController;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	MenuCategoryMasterRepository menuCategoryMasterRepository;

	@Autowired
	MenuSubCategoryMasterRepository menuSubCategoryMasterRepository;

	@Autowired
	KitchenAreaMasterRepository kitchenAreaMasterRepository;

	@Autowired
	MenuItemMasterRepository menuItemMasterRepository;

	@Autowired
	MenuItemMasterMapper menuItemMasterMapper;

	@Autowired
	CommonService commonService;

	@Autowired
	Environment environment;

	@Autowired
	ContactCategoryMasterRepository contactCategoryMasterRepository;

	@Autowired
	ContactCategoryMasterMapper categoryMasterMapper;

	@Autowired
	UnitMasterMapper unitMasterMapper;

	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Autowired
	UnitMasterRepository unitMasterRepository;

	@Autowired
	MenuItemAllocationConfigMapper menuItemAllocationConfigMapper;

	@Autowired
	MenuItemAllocationConfigRepository menuItemAllocationConfigRepository;

	@Autowired
	MenuItemRawMaterialMapper menuItemRawMaterialMapper;

	@Autowired
	MenuItemRawMaterialRepository menuItemRawMaterialRepository;

	@Autowired
	RawMaterialMasterRepository rawMaterialMasterRepository;

	@Autowired
	MenuItemRawMaterialRateDishCostingRepository menuItemRawMaterialRateDishCostingRepository;

	@Autowired
	DatabasePlanningService databasePlanningService;

	@Autowired
	PartyMasterMapper partyMasterMapper;

	@Autowired
	MenuCategoryMasterMapper menuCategoryMasterMapper;

	@Autowired
	UserFileService userFileService;

	@Autowired
	MenuSubCategoryMasterMapper subCategoryMasterMapper;

	@Autowired
	MenuPreparationDetailsRepository menuPreparationDetailsRepository;

	@Autowired
	MenuItemCaptainReceipeRepository menuItemCaptainReceipeRepository;

	@Autowired
	CaptainReceipeMasterRepository captainReceipeMasterRepository;

	MenuItemMasterServiceImpl(AdminTemplateModuleController adminTemplateModuleController) {
		this.adminTemplateModuleController = adminTemplateModuleController;
	}

	@Autowired
	UnitMasterService unitMasterService;

	@Override
	@Transactional
	public MenuItemMasterResponseDto addOrUpdateMenuItemMaster(@Valid MenuItemMasterRequestDto request, long id,
			MultipartFile file) {

		UserMasterEntity user = getUser(request.getUserId());
		MenuCategoryMasterEntity category = getCategory(request.getMenuCategoryId());
		MenuSubCategoryMasterEntity subCategory = getSubCategory(request.getMenuSubCategoryId());
//	    KitchenAreaMasterEntity kitchen = getKitchen(request.getKitchenAreaId());
		validateDuplicateMenuItem(request, user, id);

		MenuItemMasterEntity item = createOrUpdateMenuItem(request, user, category, subCategory, null, id);
		item = menuItemMasterRepository.save(item);

		if (file != null && !file.isEmpty()) {
			try {
				userFileService.storeFile(user.getId(), ModuleName.MENUITEM.toString(), item.getId(),
						FileType.IMAGE.toString(), file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		handleAllocationConfig(request, user, item);
		handleRawMaterials(request, user, item);
		handleCaptainReceipe(request, user, item);
		handleDishCosting(request, user, item);

		return menuItemMasterMapper.entityToResponse(item);
	}

	private UserMasterEntity getUser(Long id) {
		return userMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + id));
	}

	private MenuCategoryMasterEntity getCategory(Long id) {
		return menuCategoryMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Menu Category not found with id: " + id));
	}

	private MenuSubCategoryMasterEntity getSubCategory(Long id) {
		if (id != null) {
			return menuSubCategoryMasterRepository.findByIdAndIsDeleteFalse(id)
					.orElseThrow(() -> new RuntimeException("Menu SubCategory not found with id: " + id));
		} else {
			return null;
		}
	}

	private KitchenAreaMasterEntity getKitchen(Long id) {
		return kitchenAreaMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Kitchen Area not found with id: " + id));
	}

	private void validateDuplicateMenuItem(MenuItemMasterRequestDto request, UserMasterEntity user, long id) {
		List<MenuItemMasterEntity> existing = menuItemMasterRepository
				.findByNameEnglishAndUserAndIsDeleteFalse(request.getNameEnglish(), user);

		if (!existing.isEmpty() && (id == -1 || !existing.get(0).getId().equals(id))) {
			throw new RuntimeException("Menu Item '" + request.getNameEnglish() + "' already exists for this user.");
		}
	}

	private MenuItemMasterEntity createOrUpdateMenuItem(MenuItemMasterRequestDto request, UserMasterEntity user,
			MenuCategoryMasterEntity category, MenuSubCategoryMasterEntity subCategory, KitchenAreaMasterEntity kitchen,
			long id) {

		MenuItemMasterEntity entity;

		if (id == -1) {
			entity = new MenuItemMasterEntity();
			entity.setCreatedAt(commonService.getCurrentDateTime());

			if (request.getSequence() != null) {
				menuItemMasterRepository.shiftSequencesForUser(user.getId(), request.getSequence());
				entity.setSequence(request.getSequence());
			} else {
				Integer lastSeq = menuItemMasterRepository.findMaxSequenceByUserAndIsDeleteFalse(user.getId());
				entity.setSequence(lastSeq == null ? 1 : lastSeq + 1);
			}

			entity.setUuid(databasePlanningService.getOrCreateUserUuid(user));
		} else {
			entity = menuItemMasterRepository.findByIdAndIsDeleteFalse(id)
					.orElseThrow(() -> new RuntimeException("Menu Item not found with id: " + id));

			entity.setUpdatedAt(commonService.getCurrentDateTime());

			if (request.getSequence() != null && !request.getSequence().equals(entity.getSequence())) {
				List<MenuItemMasterEntity> conflicts = menuItemMasterRepository
						.findByUserAndSequenceAndIsDeleteFalse(user, request.getSequence());

				if (!conflicts.isEmpty() && !conflicts.get(0).getId().equals(entity.getId())) {
					menuItemMasterRepository.shiftSequencesForUser(user.getId(), request.getSequence());
				}

				entity.setSequence(request.getSequence());
			}
		}

		entity.setNameEnglish(request.getNameEnglish());
		entity.setNameGujarati(request.getNameGujarati());
		entity.setNameHindi(request.getNameHindi());
		entity.setSlogan(request.getSlogan());
		entity.setInstructionEnglish(request.getInstructionEnglish());
		entity.setInstructionGujarati(request.getInstructionGujarati());
		entity.setInstructionHindi(request.getInstructionHindi());
		entity.setUser(user);
		entity.setMenuCategory(category);
		entity.setMenuSubCategory(subCategory);
		entity.setRemarks(request.getRemarks());
		entity.setUrl(request.getUrl());
//	    entity.setKitchenArea(kitchen);
		entity.setIsPublished(false);
		entity.setPrice(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO);
		
		return entity;
	}

	private void handleAllocationConfig(MenuItemMasterRequestDto request, UserMasterEntity user,
			MenuItemMasterEntity item) {

		MenuItemAllocationConfigRequestDto req = request.getMenuItemAllocationConfigRequest();

		if (req == null) {
			return;
		}

		MenuItemAllocationConfigEntity allocation;
		if (req.getId() == 0) {
			allocation = new MenuItemAllocationConfigEntity(); // new record
			allocation.setCreatedAt(commonService.getCurrentDateTime());
		} else {
			allocation = menuItemAllocationConfigRepository.findByIdAndIsDeleteFalse(req.getId())
					.orElseThrow(() -> new RuntimeException("Allocation Config not found"));
			allocation.setUpdatedAt(commonService.getCurrentDateTime());
		}

		PartyMasterEntity party = getParty(req.getPartyId());
		UnitMasterEntity unit = getUnit(req.getOutsideItem(), req.getInsideItem());
		ContactCategoryMasterEntity contact = getContact(req.getOutsideItem());

		allocation.setMenuItem(item);
		allocation.setUser(user);
		allocation.setGodownLocation(req.getGodownLocation());
		allocation.setRemarks(req.getRemarks());
		allocation.setParty(party);
		allocation.setUnit(unit);
		allocation.setContact(contact);

		if (req.getSelectOutsideAgency()) {

			OutsideItemRequestDto dto = req.getOutsideItem();

			allocation.setSelectOutsideAgency(true);
			allocation.setSelectInsideAgency(false);
			allocation.setSelectChefLabourAgency(false);

			allocation.setQuantityPer100Person(dto.getQuantityPer100Person());
			allocation.setBasePrice(dto.getBasePrice());
			allocation.setPricePerLabour(dto.getPricePerLabour());
			allocation.setPricePerHelper(dto.getPricePerHelper());
		}

		if (req.getSelectInsideAgency()) {

			InsideItemRequestDto dto = req.getInsideItem();

			allocation.setSelectInsideAgency(true);
			allocation.setSelectOutsideAgency(false);
			allocation.setSelectChefLabourAgency(false);
			allocation.setNumber(dto.getNumber());
		}

		if (req.getSelectChefLabourAgency()) {

			ChefLabourItemRequestDto dto = req.getChefLabourItem();

			allocation.setSelectChefLabourAgency(true);
			allocation.setSelectInsideAgency(false);
			allocation.setSelectOutsideAgency(false);

			allocation.setAllocationType(dto.getAllocation_type());
			allocation.setCounterNo(dto.getCounterNo());
			allocation.setHelperNo(dto.getHelperNo());
			allocation.setPricePerLabour(dto.getPricePerLabour());
			allocation.setPricePerHelper(dto.getPricePerHelper());
		}

		menuItemAllocationConfigRepository.save(allocation);
	}

	private PartyMasterEntity getParty(Long partyId) {
		if (partyId == null || partyId == 0)
			return null;

		return partyMasterRepository.findByIdAndIsDeleteFalse(partyId)
				.orElseThrow(() -> new RuntimeException("Party not found"));
	}

	private UnitMasterEntity getUnit(OutsideItemRequestDto outside, InsideItemRequestDto inside) {
		Long id = null;

		if (outside != null)
			id = outside.getUnitId();
		if (id == null || id == 0)
			return null;

		return unitMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Unit not found"));
	}

	private ContactCategoryMasterEntity getContact(OutsideItemRequestDto outside) {
		if (outside == null || outside.getContactCategoryId() == null)
			return null;

		return contactCategoryMasterRepository.findByIdAndIsDeleteFalse(outside.getContactCategoryId())
				.orElseThrow(() -> new RuntimeException("Contact Category not found"));
	}

	private void handleRawMaterials(MenuItemMasterRequestDto request, UserMasterEntity user,
			MenuItemMasterEntity item) {

		if (request.getMenuItemRawMaterials() == null || request.getMenuItemRawMaterials().isEmpty()) {
			return;
		}

		for (MenuItemRawMaterialRequestDto rawReq : request.getMenuItemRawMaterials()) {

			MenuItemRawMaterialEntity raw = rawReq.getId() == 0 ? new MenuItemRawMaterialEntity()
					: menuItemRawMaterialRepository.findById(rawReq.getId())
							.orElseThrow(() -> new RuntimeException("Menu Item Raw Material not found"));

			RawMaterialMasterEntity material = rawMaterialMasterRepository.findById(rawReq.getRawMaterialId())
					.orElseThrow(() -> new RuntimeException("Raw Material not found"));

			UnitMasterEntity unit = null;
			if (rawReq.getUnitId() != null && rawReq.getUnitId() != 0) {
				unit = unitMasterRepository.findById(rawReq.getUnitId())
						.orElseThrow(() -> new RuntimeException("Unit not found"));
			}

			raw.setRate(rawReq.getRate());
			raw.setWeight(rawReq.getWeight());
			raw.setUnit(unit);
			raw.setRawMaterial(material);
			raw.setUser(user);
			raw.setIsPublished(false);
			raw.setMenuItem(item);
			raw.setVenue(rawReq.getVenue());
			raw.setUuid(databasePlanningService.getOrCreateUserUuid(user));
			raw.setIsVisible(rawReq.getIsVisible());

			menuItemRawMaterialRepository.save(raw);
		}
	}

	private void handleCaptainReceipe(MenuItemMasterRequestDto request, UserMasterEntity user,
			MenuItemMasterEntity item) {
		List<MenuItemCaptainReceipeEntity> existingReceipes = menuItemCaptainReceipeRepository
				.findByMenuItem_IdAndIsDeleteFalse(item.getId());

		Set<Long> requestIds = request.getCaptainReceipes() == null ? new HashSet<>()
				: request.getCaptainReceipes().stream().map(MenuItemCaptainReceipeRequestDto::getId)
						.filter(id -> id != null && id != 0).collect(Collectors.toSet());

		for (MenuItemCaptainReceipeEntity existing : existingReceipes) {
			if (!requestIds.contains(existing.getId())) {
				existing.setIsDelete(true);
				menuItemCaptainReceipeRepository.save(existing);
			}
		}

		if (request.getCaptainReceipes() == null || request.getCaptainReceipes().isEmpty()) {
			return;
		}

		for (MenuItemCaptainReceipeRequestDto cptReceipe : request.getCaptainReceipes()) {

			MenuItemCaptainReceipeEntity menuItemCapReceipe = cptReceipe.getId() == 0
					? new MenuItemCaptainReceipeEntity()
					: menuItemCaptainReceipeRepository.findByIdAndIsDeleteFalse(cptReceipe.getId()).orElseThrow(
							() -> new RuntimeException("Captain Receipe not found with id : " + cptReceipe.getId()));

			CaptainReceipeMasterEntity captainReceipe = captainReceipeMasterRepository
					.findByIdAndIsDeleteFalse(cptReceipe.getCaptainReceipeId())
					.orElseThrow(() -> new RuntimeException("Raw Material not found"));

			UnitMasterEntity unit = null;
			if (cptReceipe.getUnitId() != null && cptReceipe.getUnitId() != 0) {
				unit = unitMasterRepository.findById(cptReceipe.getUnitId())
						.orElseThrow(() -> new RuntimeException("Unit not found"));
			}

			menuItemCapReceipe.setWeight(cptReceipe.getWeight());
			menuItemCapReceipe.setUnit(unit);
			menuItemCapReceipe.setRate(cptReceipe.getRate());
			menuItemCapReceipe.setCaptainReceipe(captainReceipe);
			menuItemCapReceipe.setMenuItem(item);
			menuItemCapReceipe.setVenue(cptReceipe.getVenue());
			menuItemCapReceipe.setUser(user);
			menuItemCapReceipe.setUuid(databasePlanningService.getOrCreateUserUuid(user));

			menuItemCaptainReceipeRepository.save(menuItemCapReceipe);
		}
	}

	private void handleDishCosting(MenuItemMasterRequestDto request, UserMasterEntity user, MenuItemMasterEntity item) {

		Optional<MenuItemRawMaterialRateDishCostingEntity> existing = menuItemRawMaterialRateDishCostingRepository
				.findByMenuItemAndUserAndIsDeleteFalse(item, user);

		MenuItemRawMaterialRateDishCostingEntity costing;

		if (existing.isPresent()) {
			costing = existing.get();
			costing.setUpdatedAt(commonService.getCurrentDateTime());
		} else {
			costing = new MenuItemRawMaterialRateDishCostingEntity();
			costing.setMenuItem(item);
			costing.setUser(user);
			costing.setUuid(databasePlanningService.getOrCreateUserUuid(user));
			costing.setCreatedAt(commonService.getCurrentDateTime());
		}

		costing.setDishCosting(request.getDishCosting());
		costing.setTotalRate(request.getTotalRate());
		costing.setIsPublished(false);

		menuItemRawMaterialRateDishCostingRepository.save(costing);
	}

	@Override
	public Page<MenuItemMasterResponseDto> getAllMenuItem(Long userId, String itemName, Pageable pageable,
			Long menuCatId, Long menuSubCatId, Boolean isAsc, Boolean isWithRecipe) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		boolean allCategories = (menuCatId == null || menuCatId == 0);
		boolean allSubCategories = (menuSubCatId == null || menuSubCatId == 0);

		Sort sort;

		if (isAsc == null) {
			sort = Sort.by("sequence").ascending();
		} else {
			sort = isAsc ? Sort.by("nameEnglish").ascending() : Sort.by("nameEnglish").descending();
		}

		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

		Page<MenuItemMasterEntity> menuItems;
		if (!isEmpty(itemName)) {
			menuItems = menuItemMasterRepository.searchMenuItemsDynamic(itemName, user, menuCatId, menuSubCatId,
					isWithRecipe, sortedPageable);

		} else {
			if (allCategories && allSubCategories) {

				if (isWithRecipe == null) {
					menuItems = menuItemMasterRepository.findAllByUserAndIsDeleteFalse(user, sortedPageable);
				} else {
					menuItems = menuItemMasterRepository.findAllByUserAndRecipeFilter(user, isWithRecipe,
							sortedPageable);
				}

			} else if (!allCategories && allSubCategories) {

				menuItems = menuItemMasterRepository.findAllByUserAndMenuCategory_IdAndIsDeleteFalseAndRecipeFilter(
						user, menuCatId, isWithRecipe, sortedPageable);

			} else if (allCategories && !allSubCategories) {

				menuItems = menuItemMasterRepository.findAllByUserAndMenuSubCategory_IdAndIsDeleteFalseAndRecipeFilter(
						user, menuSubCatId, isWithRecipe, sortedPageable);

			} else {

				menuItems = menuItemMasterRepository
						.findAllByUserAndMenuCategory_IdAndMenuSubCategory_IdAndIsDeleteFalseAndRecipeFilter(user,
								menuCatId, menuSubCatId, isWithRecipe, sortedPageable);
			}
		}
		Page<MenuItemMasterResponseDto> res = menuItems.map(entity -> buildMenuItemResponse(entity, userId));
		
		return res;
	}

	@Override
	public MenuItemMasterResponseDto getMenuItemById(Long id) {

		MenuItemMasterEntity entity = menuItemMasterRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Menu Item not found with id: " + id));

		MenuItemMasterResponseDto responseDto = menuItemMasterMapper.entityToResponse(entity);

		responseDto.setImagePath(buildImageUrl(entity.getImagePath()));
		responseDto.setCreatedAt(formatDate(entity.getCreatedAt()));
		responseDto.setUserId(entity.getUser().getId());

		responseDto.setMenuItemRawMaterials(getRawMaterials(entity.getId()));
		responseDto.setMenuItemAllocationConfigs(getAllocationConfigs(entity));
		responseDto.setMenuItemCaptainReceipe(getMenuItemCaptainReceipe(entity.getId()));

		MenuItemRawMaterialRateDishCostingEntity costing = menuItemRawMaterialRateDishCostingRepository
				.findByMenuItemAndIsDeleteFalse(entity);

		if (costing != null) {
			responseDto.setTotalRate(costing.getTotalRate());
			responseDto.setDishCosting(costing.getDishCosting());
		} else {
			responseDto.setTotalRate(BigDecimal.ZERO);
			responseDto.setDishCosting(BigDecimal.ZERO);
		}

		return responseDto;
	}
	
	private MenuItemMasterResponseDto buildMenuItemResponse(MenuItemMasterEntity entity, Long userId) {
		MenuItemMasterResponseDto dto = menuItemMasterMapper.entityToResponse(entity);

		dto.setImagePath(buildImageUrl(entity.getImagePath()));
		dto.setCreatedAt(formatDate(entity.getCreatedAt()));
		dto.setUserId(userId);
		dto.getMenuCategory().setImagePath(buildImageUrl(entity.getMenuCategory().getImagePath()));
		dto.setMenuItemRawMaterials(getRawMaterials(entity.getId()));
		dto.setMenuItemCaptainReceipe(getMenuItemCaptainReceipe(entity.getId()));
		dto.setMenuItemAllocationConfigs(getAllocationConfigs(entity));
		MenuItemRawMaterialRateDishCostingEntity cost = menuItemRawMaterialRateDishCostingRepository
				.findByMenuItemAndIsDeleteFalse(entity);
		if (cost != null) {
			dto.setTotalRate(cost.getTotalRate());
			dto.setDishCosting(cost.getDishCosting());
		} else {
			dto.setTotalRate(BigDecimal.ZERO);
			dto.setDishCosting(BigDecimal.ZERO);
		}
		return dto;
	}

	private String buildImageUrl(String imagePath) {
		if (imagePath == null || imagePath.isEmpty()) {
			return "";
		}
		return environment.getProperty("app.image.url") + imagePath;
	}

	private String formatDate(LocalDateTime date) {
		if (date == null) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return date.format(formatter);
	}

	private List<MenuItemRawMaterialsResponseDto> getRawMaterials(Long menuItemId) {

		return menuItemRawMaterialRepository.findAllByMenuItem_IdAndIsDeleteFalse(menuItemId).stream()
				.map(menuItemRawMaterialMapper::entityToResponse).collect(Collectors.toList());
	}

	private List<MenuItemCaptainReceipeResponseDto> getMenuItemCaptainReceipe(Long menuItemId) {

		List<MenuItemCaptainReceipeEntity> entities = menuItemCaptainReceipeRepository
				.findAllByMenuItem_IdAndIsDeleteFalse(menuItemId);

		List<MenuItemCaptainReceipeResponseDto> response = new ArrayList<>();

		for (MenuItemCaptainReceipeEntity entity : entities) {
			MenuItemCaptainReceipeResponseDto dto = new MenuItemCaptainReceipeResponseDto();

			dto.setId(entity.getId());
			dto.setWeight(entity.getWeight());
			dto.setUnitId(entity.getUnit().getId());
			dto.setUnitHierarchy(unitMasterService.getParentUnitsWithChildren(entity.getUnit().getId()));
			dto.setRate(entity.getRate() != null ? entity.getRate() : BigDecimal.ZERO);
			dto.setWeight(entity.getWeight() != null ? entity.getWeight() : BigDecimal.ZERO);
			dto.setUnitId(entity.getUnit() != null ? entity.getUnit().getId() : null);
			dto.setUnitName(entity.getUnit() != null ? entity.getUnit().getNameEnglish() : null);
			dto.setUnitHierarchy(
					entity.getUnit() != null ? unitMasterService.getParentUnitsWithChildren(entity.getUnit().getId())
							: null);
			dto.setCaptainReceipeMaster(mapCaptainReceipe(entity.getCaptainReceipe()));
			dto.setVenue(entity.getVenue());
			dto.setMenuItemId(entity.getMenuItem().getId());
			dto.setMenuItemName(entity.getMenuItem().getNameEnglish());
			dto.setIsDelete(entity.getIsDelete());
			dto.setIsActive(entity.getIsActive());
			dto.setCreatedAt(entity.getCreatedAt());
			dto.setUpdatedAt(entity.getUpdatedAt());
			dto.setUserId(entity.getUser().getId());
			dto.setUuid(entity.getUuid());

			response.add(dto);
		}

		return response;
	}

//	private MenuItemMasterResponseDto mapMenuItem(MenuItemMasterEntity entity) {
//
//		if (entity == null) {
//			return null;
//		}
//
//		MenuItemMasterResponseDto dto = new MenuItemMasterResponseDto();
//
//		dto.setId(entity.getId());
//		dto.setNameEnglish(entity.getNameEnglish());
//		dto.setNameGujarati(entity.getNameGujarati());
//		dto.setNameHindi(entity.getNameHindi());
//		dto.setSlogan(entity.getSlogan());
//		dto.setPrice(entity.getPrice());
//		dto.setSequence(entity.getSequence());
//		dto.setImagePath(entity.getImagePath());
//		dto.setIsActive(entity.getIsActive());
//		dto.setUserId(entity.getUser().getId());
//		dto.setUrl(entity.getUrl());
//		dto.setRemarks(entity.getRemarks());
//
//		if (entity.getCreatedAt() != null) {
//			dto.setCreatedAt(commonService.dateTimeFormatted(entity.getCreatedAt()));
//		}
//
//		return dto;
//	}

	private CaptainReceipeMasterResponseDto mapCaptainReceipe(CaptainReceipeMasterEntity entity) {

		if (entity == null) {
			return null;
		}

		CaptainReceipeMasterResponseDto dto = new CaptainReceipeMasterResponseDto();

		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setUserId(entity.getUserId());
		dto.setWeight(entity.getWeight());
		dto.setUnitId(entity.getUnitId());
		dto.setRate(entity.getRate());
		dto.setIsActive(entity.getIsActive());
		dto.setIsDelete(entity.getIsDelete());

		if (entity.getCreatedAt() != null) {
			dto.setCreatedAt(commonService.dateTimeFormatted(entity.getCreatedAt()));
		}

		if (entity.getUpdatedAt() != null) {
			dto.setUpdatedAt(commonService.dateTimeFormatted(entity.getUpdatedAt()));
		}

		return dto;
	}

	private MenuItemAllocationConfigResponseDto getAllocationConfigs(MenuItemMasterEntity entity) {

		List<MenuItemAllocationConfigEntity> configs = menuItemAllocationConfigRepository
				.findAllByMenuItemAndIsDeleteFalse(entity);

		if (configs.isEmpty()) {
			return null;
		}

		return mapAllocationConfig(configs.get(0));
	}

	private MenuItemAllocationConfigResponseDto mapAllocationConfig(MenuItemAllocationConfigEntity ac) {

		MenuItemAllocationConfigResponseDto res = new MenuItemAllocationConfigResponseDto();

		res.setId(ac.getId());
		res.setGodownLocation(ac.getGodownLocation());
		res.setRemarks(ac.getRemarks());

		res.setSelectChefLabourAgency(ac.getSelectChefLabourAgency());
		res.setSelectOutsideAgency(ac.getSelectOutsideAgency());
		res.setSelectInsideAgency(ac.getSelectInsideAgency());

		res.setParty(ac.getParty() != null ? partyMasterMapper.entityToResponse2(ac.getParty()) : null);

		if (Boolean.TRUE.equals(ac.getSelectChefLabourAgency())) {
			ChefLabourItemResponseDto chef = new ChefLabourItemResponseDto();
			chef.setAllocation_type(ac.getAllocationType());
			chef.setCounterNo(ac.getCounterNo());
			chef.setPricePerLabour(ac.getPricePerLabour());
			chef.setPricePerHelper(ac.getPricePerHelper());
			chef.setBasePrice(ac.getBasePrice());
			chef.setQtyPer100Person(ac.getQuantityPer100Person());
			res.setChefLabourItem(chef);
		}

		if (Boolean.TRUE.equals(ac.getSelectOutsideAgency())) {
			OutsideItemResponseDto out = new OutsideItemResponseDto();
			out.setQuantityPer100Person(ac.getQuantityPer100Person());
			out.setBasePrice(ac.getBasePrice());
			out.setPricePerLabour(ac.getPricePerLabour());
			out.setPricePerHelper(ac.getPricePerHelper());

			out.setContactCategory(
					ac.getContact() != null ? categoryMasterMapper.entityToResponse(ac.getContact()) : null);
			out.setUnit(ac.getUnit() != null ? unitMasterMapper.entityToResponse(ac.getUnit()) : null);

			res.setOutsideItem(out);
		}

		if (Boolean.TRUE.equals(ac.getSelectInsideAgency())) {
			InsideItemResponseDto inside = new InsideItemResponseDto();
			inside.setNumber(ac.getNumber());
			res.setInsideItem(inside);
		}

		return res;
	}

	private boolean isEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}

	@Override
	public boolean updateMenuItemStatus(Long id, Boolean isActive) {

		Optional<MenuItemMasterEntity> entityOpt = menuItemMasterRepository.findByIdAndIsDeleteFalse(id);
		if (!entityOpt.isPresent()) {
			throw new RuntimeException("Menu Item not found with id: " + id);
		}

		MenuItemMasterEntity entity = entityOpt.get();
		entity.setIsActive(isActive);
		menuItemMasterRepository.save(entity);
		return true;

	}

	@Override
	public Boolean deleteMenuItemById(Long id) {

		Optional<MenuItemMasterEntity> entityOptional = menuItemMasterRepository.findByIdAndIsDeleteFalse(id);

		if (!entityOptional.isPresent()) {
			throw new RuntimeException("Menu Item not found with id: " + id);
		}

		MenuItemMasterEntity entity = entityOptional.get();
		if (menuPreparationDetailsRepository.existsByMenuItem(entity)) {
			throw new RuntimeException("Menu item already exists in menu planning. Please delete it first.");
		}
		MenuItemAllocationConfigEntity allocationConfigEntity = menuItemAllocationConfigRepository
				.findByMenuItem(entity);
		System.out.println("in");
		if (allocationConfigEntity != null) {
			allocationConfigEntity.setIsDelete(true);
			menuItemAllocationConfigRepository.save(allocationConfigEntity);
		}

		List<MenuItemRawMaterialEntity> entities = menuItemRawMaterialRepository.findAllByMenuItem(entity);
		if (!entities.isEmpty()) {
			for (MenuItemRawMaterialEntity menuItemRawMaterialEntity : entities) {
				menuItemRawMaterialEntity.setIsDelete(true);
				menuItemRawMaterialRepository.save(menuItemRawMaterialEntity);
			}
		}

		Set<Long> menuItems = new HashSet<>();
		menuItems.add(entity.getId());
		List<MenuItemRawMaterialRateDishCostingEntity> costingEntities = menuItemRawMaterialRateDishCostingRepository
				.findByMenuItemIdInAndIsDeleteFalse(menuItems);
		if (!costingEntities.isEmpty()) {
			for (MenuItemRawMaterialRateDishCostingEntity menuItemRawMaterialRateDishCostingEntity : costingEntities) {
				menuItemRawMaterialRateDishCostingEntity.setIsDelete(true);
				menuItemRawMaterialRateDishCostingRepository.save(menuItemRawMaterialRateDishCostingEntity);
			}
		}
		// Delete main menu item
		entity.setIsDelete(true);
		menuItemMasterRepository.save(entity);

		return true;
	}

	@Override
	public Boolean deleteMenuItemRawmaterialById(MenuRawMaterialIdsDto ids) {

		List<Long> idList = ids.getId();

		for (Long id : idList) {
			Optional<MenuItemRawMaterialEntity> entity = menuItemRawMaterialRepository.findByIdAndIsDeleteFalse(id);
			if (!entity.isPresent()) {
				throw new RuntimeException("Menu Item Rawmaterial not found with id: " + id);
			}
			MenuItemRawMaterialEntity itemRawMaterialEntity = entity.get();
			itemRawMaterialEntity.setIsDelete(true);
			menuItemRawMaterialRepository.save(itemRawMaterialEntity);
		}
		return true;
	}

	@Override
	@Transactional
	public Boolean updateMenuItemCategory(List<Long> menuItemIds, Long newCatId, Long userId) {

		int updatedRaws = menuItemMasterRepository.updateMenuItemCategories(menuItemIds, newCatId, userId);

		return updatedRaws > 0;
	}

	@Override
	@Transactional
	public Boolean updateMenuItemSubCategory(List<Long> menuitemIds, Long newMenuSubCatId, Long userId) {
		int updatedRaws = 0;

		updatedRaws = menuItemMasterRepository.updateMenuSubCategory(menuitemIds, newMenuSubCatId, userId);

		return updatedRaws > 0;
	}

	@Override
	@Transactional
	public Boolean updateMenuAllocation(String toType, List<MenuAllocationItemRequestDto> allocationItemRequestDtos,
			Long userId) {

		int updatedRaws = 0;

		for (MenuAllocationItemRequestDto request : allocationItemRequestDtos) {
			updatedRaws = menuItemMasterRepository.updateMenuAllocation(request.getId(), request.getQuantity(),
					request.getUnitId(), request.getPrice(), userId);
		}

		return updatedRaws > 0;
	}

	@Override
	@Transactional
	public Boolean updateMenuAllocationItemConfig(List<MenuAllocationChangeRequestDto> request) {

		for (MenuAllocationChangeRequestDto requestDto : request) {
			MenuItemAllocationConfigEntity entity = new MenuItemAllocationConfigEntity();

			Optional<MenuItemAllocationConfigEntity> entityOptional = menuItemAllocationConfigRepository
					.findByMenuItemIdAndIsDeleteFalse(requestDto.getMenuItemId());

			if (entityOptional.isPresent()) {
				entity = entityOptional.get();
			} else {
				entity = menuItemAllocationConfigMapper.requestToEntity(requestDto);
			}

			entity = menuItemAllocationConfigMapper.updateEntityFromRequest(requestDto, entity);

			MenuItemMasterEntity menuEntity = menuItemMasterRepository
					.findByIdAndIsDeleteFalse(requestDto.getMenuItemId())
					.orElseThrow(() -> new RuntimeException("Menu not found with id : " + requestDto.getMenuItemId()));

			Optional<PartyMasterEntity> partyEntityOptional = partyMasterRepository
					.findByIdAndIsDeleteFalse(requestDto.getPartyId());

			Optional<UnitMasterEntity> unitEntityOptional = unitMasterRepository
					.findByIdAndIsDeleteFalse(requestDto.getUnitId());

			UserMasterEntity userEntity = userMasterRepository.findByIdAndIsDeleteFalse(requestDto.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found with id : " + requestDto.getUserId()));

			Optional<ContactCategoryMasterEntity> contactCategoryEntity = contactCategoryMasterRepository
					.findByIdAndIsDeleteFalse(requestDto.getContactCategoryId());

			entity.setMenuItem(menuEntity);
			entity.setParty(partyEntityOptional.isPresent() ? partyEntityOptional.get() : null);
			entity.setUnit(unitEntityOptional.isPresent() ? unitEntityOptional.get() : null);
			entity.setUser(userEntity);
			entity.setContact(contactCategoryEntity.isPresent() ? contactCategoryEntity.get() : null);
			entity.setUpdatedAt(LocalDateTime.now());

			menuItemAllocationConfigRepository.save(entity);
		}

		return true;
	}

	@Override
	public List<MenuItemCategoryChangeResponseDto> getAllMenuItemsByCategory(List<Long> catIds, List<Long> subcatIds,
			Long userId, String type) {

		List<Object[]> entities = new ArrayList<>();
		Boolean outside = false;
		Boolean cheflabour = false;
		Boolean inside = false;

		if (subcatIds == null || subcatIds.isEmpty())
			subcatIds = null;

		if (type != null && !type.isEmpty() && type.equalsIgnoreCase("inside")) {
			inside = true;
		} else if (type != null && !type.isEmpty() && type.equalsIgnoreCase("outside")) {
			outside = true;
		} else if (type != null && !type.isEmpty() && type.equalsIgnoreCase("cheflabour")) {
			cheflabour = true;
		}

		entities = menuItemMasterRepository
				.findByMenuCategoryIdInAndMenuSubCategoryIdInAndUserIdAndIsDeleteFalseAndSelectAgency(catIds, subcatIds,
						userId, inside, outside, cheflabour, type == null ? null : type.toUpperCase());
		System.out.println("entities size : " + entities.size());
		return entities.stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	public MenuItemCategoryChangeResponseDto mapToResponse(Object[] row) {
		MenuItemCategoryChangeResponseDto dto = new MenuItemCategoryChangeResponseDto();

		int index = 0;

		dto.setId(commonService.getLong(row[index++]));
		dto.setNameEnglish(commonService.getString(row[index++]));
		dto.setNameGujarati(commonService.getString(row[index++]));
		dto.setNameHindi(commonService.getString(row[index++]));
		dto.setMenuCategoryId(commonService.getLong(row[index++]));
		dto.setMenuCategoryNameEnglish(commonService.getString(row[index++]));
		dto.setMenuCategoryNameGujarati(commonService.getString(row[index++]));
		dto.setMenuCategoryNameHindi(commonService.getString(row[index++]));
		dto.setMenuSubCategoryId(commonService.getLong(row[index++]));
		dto.setMenuSubCategoryNameEnglish(commonService.getString(row[index++]));
		dto.setMenuSubCategoryNameGujarati(commonService.getString(row[index++]));
		dto.setMenuSubCategoryNameHindi(commonService.getString(row[index++]));
		dto.setMenuItemAllocationId(commonService.getLong(row[index++]));
		dto.setAllocationType(commonService.getString(row[index++]));
		dto.setBase_price(commonService.getBigDecimal(row[index++]));
		dto.setCounterNo(commonService.getString(row[index++]));
		dto.setGodownLocation(commonService.getString(row[index++]));
		dto.setPricePerHelper(commonService.getBigDecimal(row[index++]));
		dto.setPricePerLabour(commonService.getBigDecimal(row[index++]));
		dto.setQtyPer100Person(commonService.getBigDecimal(row[index++]));
		dto.setChefLabourAgency(commonService.getBoolean(row[index++]));
		dto.setOutsideAgency(commonService.getBoolean(row[index++]));
		dto.setInsideAgency(commonService.getBoolean(row[index++]));
		dto.setSupplierId(commonService.getLong(row[index++]));
		dto.setSupplierNameEnglish(commonService.getString(row[index++]));
		dto.setSupplierNameHindi(commonService.getString(row[index++]));
		dto.setSupplierNameGujarati(commonService.getString(row[index++]));
		dto.setContactCategoryId(commonService.getLong(row[index++]));
		dto.setContactNameEnglish(commonService.getString(row[index++]));
		dto.setContactNameHindi(commonService.getString(row[index++]));
		dto.setContactNameGujarati(commonService.getString(row[index++]));
		dto.setUnitId(commonService.getLong(row[index++]));
		dto.setUnitNameEnglish(commonService.getString(row[index++]));
		dto.setUnitNameGujarati(commonService.getString(row[index++]));
		dto.setUnitNameHindi(commonService.getString(row[index++]));
		dto.setSymbolEnglish(commonService.getString(row[index++]));
		dto.setSymbolGujarati(commonService.getString(row[index++]));
		dto.setSymbolHindi(commonService.getString(row[index++]));
		dto.setUser(commonService.getLong(row[index++]));
		dto.setNumber(commonService.getString(row[index++]));
		dto.setRemarks(commonService.getString(row[index++]));
		dto.setHelperNo(commonService.getString(row[index++]));

		return dto;
	}

	@Override
	public List<ExistingItemRawResponseDto> getAllExistingItems(Long userId,Boolean isCaptainRecipe) {
		if(isCaptainRecipe) {
			return menuItemMasterRepository.getAllExistingItemsForCaptainRecipe(userId);	
		}else {
			return menuItemMasterRepository.getAllExistingItems(userId);			
		}
	}

}
