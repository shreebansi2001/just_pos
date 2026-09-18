package com.crmportal.service.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.MenuItemRawMaterialEntity;
import com.crmportal.entity.MenuItemRawMaterialRateDishCostingEntity;
import com.crmportal.entity.RawMaterialCategoryMasterEntity;
import com.crmportal.entity.RawMaterialCategoryTypeMasterEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UnitRangeEntity;
import com.crmportal.entity.UnitStepwiseRangeEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.ERangeType;
import com.crmportal.repository.DatabasePlanningEntityRepository;
import com.crmportal.repository.MenuCategoryMasterRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.MenuItemRawMaterialRateDishCostingRepository;
import com.crmportal.repository.MenuItemRawMaterialRepository;
import com.crmportal.repository.RawMaterialCategoryMasterRepository;
import com.crmportal.repository.RawMaterialCategoryTypeMasterRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.repository.UnitRangeRepository;
import com.crmportal.repository.UnitStepwiseRangeRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.DBExcelRequestDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.DatabaseExcelParserService;
import com.crmportal.service.DatabasePlanningService;
import com.crmportal.utility.ExcelUtility;
import com.crmportal.utility.LanguageUtils;
import com.crmportal.utility.ResponseUtils;

@Service
public class DatabaseExcelParserServiceImpl implements DatabaseExcelParserService {
	@Autowired
	private RawMaterialCategoryMasterRepository rawMaterialCategoryMasterRepository;
	@Autowired
	private RawMaterialMasterRepository rawMaterialMasterRepository;
	@Autowired
	private MenuCategoryMasterRepository menuCategoryMasterRepository;
	@Autowired
	private RawMaterialCategoryTypeMasterRepository rawMaterialCategoryTypeMasterRepository;
	@Autowired
	private UserMasterRepository userMasterRepository;
	@Autowired
	private UnitMasterRepository unitMasterRepository;
	@Autowired
	private MenuItemMasterRepository menuItemMasterRepository;
	@Autowired
	private MenuItemRawMaterialRepository menuItemRawMaterialRepository;
	@Autowired
	private DatabasePlanningService databasePlanningService;
	@Autowired
	CommonService commonService;
	@Autowired
	UnitRangeRepository unitRangeRepository;
	@Autowired
	UnitStepwiseRangeRepository unitStepwiseRangeRepository;
	@Autowired
	MenuItemRawMaterialRateDishCostingRepository costingRepository;
	@Autowired
	DatabasePlanningEntityRepository databasePlanningEntityRepository;

	@Autowired
	MenuItemRawMaterialServiceImpl menuItemRawMaterialServiceImpl;
	private static final Map<String, Integer> TABLE_WITH_ALLOWED_EMPTY_CELLS;
	private static final String MENU_CATEGORY_IMAGE_PATH = "/jcupload/%s/MENUCATEGORY/%s.jpg";
	private static final String MEMU_ITEMS_IMAGE_PATH = "/jcupload/%s/MENUITEM/%s.jpg";

	static {
		Map<String, Integer> tmp = new HashMap<>();
		tmp.put("raw_material_category", 4);
		tmp.put("rawmaterial", 6);
		tmp.put("menucategory", 4);
		tmp.put("memuitems", 5);
		TABLE_WITH_ALLOWED_EMPTY_CELLS = Collections.unmodifiableMap(tmp);
	}

	@Override
	public Map<String, Object> parseExcelToDb(InputStream inputStream, DBExcelRequestDto dbExcelRequestDto) {
		String message = "Data saved with errors";
		Map<String, List<String>> errorMap = new LinkedHashMap<>();
		Long dbPlanningId = null;
		try {
			System.err.println("inside try");
			String uuid = UUID.randomUUID().toString();
			UserMasterEntity userMasterEntity = getUserMasterEntity(Long.parseLong(dbExcelRequestDto.getUserId()));
			if (Objects.isNull(userMasterEntity)) {
				return ResponseUtils.createFailedRespones("User not found",
						"User not found for userId " + dbExcelRequestDto.getUserId());
			}

			dbPlanningId = databasePlanningService.saveDatabasePlanningEntity(dbExcelRequestDto, uuid, errorMap);
			if (Objects.isNull(dbPlanningId)) {
				return ResponseUtils.createFailedRespones("Data not saved", "Data not saved in dbPlanning table");
			}

			Map<String, List<List<String>>> excel = ExcelUtility.readExcel(inputStream, 8);
			excel = formateRows(excel);

			saveUnitMaster(excel.get("Unit"), userMasterEntity, uuid, errorMap, dbPlanningId);
			saveUnitRangesMaster(excel.get("unit_range_precision"), userMasterEntity, uuid, errorMap, dbPlanningId);
			saveUnitStepsMaster(excel.get("unit_stepwise"), userMasterEntity, uuid, errorMap, dbPlanningId);
			saveRawMaterialCategoryMasterEntity(excel.get("raw_material_category"), userMasterEntity, uuid, errorMap);
			saveRawMaterialMasterEntity(excel.get("rawmaterial"), userMasterEntity, uuid, errorMap);
			saveMenuCategoryMasterEntity(excel.get("menucategory"), userMasterEntity, uuid, errorMap, dbPlanningId);
			saveMenuItemMasterEntity(excel.get("memuitems"), userMasterEntity, uuid, errorMap, dbPlanningId);
			saveMenuItemRawMaterialEntity(excel.get("menu_item_raw_material"), userMasterEntity, uuid, errorMap);
			saveMenuItemRawMaterialRateDishCostingEntity(userMasterEntity, uuid);

			if (errorMap.isEmpty()) {
				return ResponseUtils.createSuccessRespones("Data Saved", "Excel Data Upload Successfull");
			} else {
				deleteDbPlanningEntry(dbPlanningId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			message = "Excel Data uploadation failed with message " + e.getLocalizedMessage();
			deleteDbPlanningEntry(dbPlanningId);
		}
		return ResponseUtils.createFailedRespones(message, errorMap);
	}

	@Transactional
	private void saveUnitStepsMaster(List<List<String>> list, UserMasterEntity userMasterEntity, String uuid,
			Map<String, List<String>> errorMap, Long dbPlanningId) {

		Map<String, UnitMasterEntity> unitMap = unitMasterRepository.findByUuid(uuid).stream().collect(
				Collectors.toMap(UnitMasterEntity::getNameEnglish, Function.identity(), (existing, duplicate) -> {
					errorMap.computeIfAbsent("unitstep", k -> new ArrayList<>())
							.add("Duplicate Unit found in DB: " + duplicate.getNameEnglish());
					return existing;
				}));

		processBulkUpload(list, "unitstep", errorMap,
				i -> createUnitStepwiseRangeMaster(list.get(i), i, userMasterEntity, uuid, dbPlanningId, unitMap),
				unitStepwiseRangeRepository);
	}

	private UnitStepwiseRangeEntity createUnitStepwiseRangeMaster(List<String> list, int rowIndex,
			UserMasterEntity userMasterEntity, String uuid, Long dbPlanningId, Map<String, UnitMasterEntity> unitMap) {

		UnitStepwiseRangeEntity stepwiseRangeEntity = new UnitStepwiseRangeEntity();

		stepwiseRangeEntity.setIsActive(true);
		stepwiseRangeEntity.setIsDelete(false);

		stepwiseRangeEntity.setStepValue(Double.valueOf(list.get(0)));

		UnitMasterEntity unit = unitMap.get(list.get(1));

		if (unit == null) {
			throw new RuntimeException("Unit not found at row " + (rowIndex + 1));
		}

		stepwiseRangeEntity.setUnit(unit);
		stepwiseRangeEntity.setUuid(uuid);

		return stepwiseRangeEntity;
	}

	@Transactional
	private void saveUnitRangesMaster(List<List<String>> list, UserMasterEntity userMasterEntity, String uuid,
			Map<String, List<String>> errorMap, Long dbPlanningId) {

		Map<String, UnitMasterEntity> unitMap = unitMasterRepository.findByUuid(uuid).stream().collect(
				Collectors.toMap(UnitMasterEntity::getNameEnglish, Function.identity(), (existing, duplicate) -> {
					errorMap.computeIfAbsent("unitrange", k -> new ArrayList<>())
							.add("Duplicate Unit found in DB: " + duplicate.getNameEnglish());
					return existing;
				}));

		processBulkUpload(list, "unitrange", errorMap,
				i -> createUnitRangeMaster(list.get(i), i, userMasterEntity, uuid, dbPlanningId, unitMap),
				unitRangeRepository);
	}

	private UnitRangeEntity createUnitRangeMaster(List<String> list, int rowIndex, UserMasterEntity userMasterEntity,
			String uuid, Long dbPlanningId, Map<String, UnitMasterEntity> unitMap) {

		UnitRangeEntity rangeEntity = new UnitRangeEntity();

		rangeEntity.setIsActive(true);
		rangeEntity.setIsDelete(false);

		rangeEntity.setMinValue(Double.valueOf(list.get(0)));
		rangeEntity.setMaxValue(Double.valueOf(list.get(1)));
		rangeEntity.setRoundOffValue(Double.valueOf(list.get(2)));

		UnitMasterEntity unit = unitMap.get(list.get(3));

		if (unit == null) {
			throw new RuntimeException("Unit not found at row " + (rowIndex + 1));
		}

		rangeEntity.setUnit(unit);
		rangeEntity.setRangeType(ERangeType.valueOf(list.get(4)));
		rangeEntity.setUuid(uuid);

		return rangeEntity;
	}

	@Transactional
	private void saveMenuItemRawMaterialRateDishCostingEntity(UserMasterEntity userMasterEntity, String uuid) {

		List<Object[]> rateDishCosting = menuItemRawMaterialRepository.getRateDishCosting(userMasterEntity.getId(),
				uuid);

		if (rateDishCosting == null || rateDishCosting.isEmpty()) {
			return;
		}

		Set<Long> menuItemIds = rateDishCosting.stream()
				.map(row -> row[0] == null ? null : ((Number) row[0]).longValue()).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		Map<Long, MenuItemMasterEntity> menuItemMap = menuItemMasterRepository.findAllById(menuItemIds).stream()
				.filter(item -> !Boolean.TRUE.equals(item.getIsDelete()))
				.collect(Collectors.toMap(MenuItemMasterEntity::getId, m -> m));

		List<MenuItemRawMaterialRateDishCostingEntity> costingEntities = new ArrayList<>(rateDishCosting.size());

		for (Object[] row : rateDishCosting) {

			MenuItemRawMaterialRateDishCostingEntity entity = new MenuItemRawMaterialRateDishCostingEntity();

			entity.setCreatedAt(commonService.getCurrentDateTime());
			entity.setIsActive(true);
			entity.setIsDelete(false);
			entity.setUuid(uuid);
			entity.setUser(userMasterEntity);

			Long menuItemId = row[0] == null ? null : ((Number) row[0]).longValue();

			if (menuItemId != null) {
				entity.setMenuItem(menuItemMap.get(menuItemId));
			}

			entity.setTotalRate(row[1] == null ? BigDecimal.ZERO : new BigDecimal(row[1].toString()));

			entity.setDishCosting(row[2] == null ? BigDecimal.ZERO : new BigDecimal(row[2].toString()));

			costingEntities.add(entity);
		}

		costingRepository.saveAll(costingEntities);
	}

	@Transactional
	private void saveUnitMaster(List<List<String>> list, UserMasterEntity userMasterEntity, String uuid,
			Map<String, List<String>> errorMap, Long dbPlanningId) {

		for (int i = 1; i < list.size(); i++) {

			try {
				Set<String> existingNames = unitMasterRepository
						.findByUuidAndUserAndIsDeleteFalse(uuid, userMasterEntity).stream()
						.map(UnitMasterEntity::getNameEnglish).collect(Collectors.toSet());
				UnitMasterEntity entity = createUnitMaster(list.get(i), i, userMasterEntity, uuid, dbPlanningId,
						existingNames);

				unitMasterRepository.save(entity);

			} catch (Exception e) {

				errorMap.computeIfAbsent("unit", k -> new ArrayList<>()).add("Row " + (i + 1) + " : " + e.getMessage());
			}
		}
	}

	private UnitMasterEntity createUnitMaster(List<String> list, int rowIndex, UserMasterEntity userMasterEntity,
			String uuid, Long dbPlanningId, Set<String> existingNames) {

		UnitMasterEntity entity = new UnitMasterEntity();

		String nameEnglish = list.get(0);

		if (existingNames.contains(nameEnglish)) {
			throw new RuntimeException("Duplicate Unit: " + nameEnglish);
		}

		entity.setNameEnglish(list.get(0));
		entity.setSymbolEnglish(list.get(1));
		System.out.println("in:-" + entity.getNameEnglish());
		Boolean isParent = Boolean.parseBoolean(list.get(2));
		String parentUnitName = list.get(3);
		String equivalentValueStr = list.get(4);
		String value = list.get(5);

		if (value != null && !value.trim().isEmpty()) {
			entity.setDecimalLimit((int) Double.parseDouble(value));
		}

		entity.setNameGujarati(list.get(6));
		entity.setNameHindi(list.get(7));
		entity.setSymbolGujarati(list.get(8));
		entity.setSymbolHindi(list.get(9));
		entity.setRangeType(ERangeType.valueOf(list.get(10)));
		entity.setUser(userMasterEntity);
		entity.setUuid(uuid);

		if (isParent) {

			entity.setIsParentUnit(true);
			entity.setParentUnit(null);
			entity.setEquivalentValue(null);

		} else {

			entity.setIsParentUnit(false);

			if (equivalentValueStr != null && !equivalentValueStr.trim().isEmpty()
					&& !equivalentValueStr.equalsIgnoreCase("NULL")) {
				System.out.println("equivalentValueStr:-" + equivalentValueStr);
				entity.setEquivalentValue(Double.valueOf(equivalentValueStr));
			}

			System.out.println("out:-" + entity.getNameEnglish());
			if (parentUnitName != null && !parentUnitName.trim().isEmpty()
					&& !parentUnitName.equalsIgnoreCase("NULL")) {
				System.out.println("parentUnitName:-" + parentUnitName);
				UnitMasterEntity parent = unitMasterRepository
						.findByNameEnglishAndUserAndIsDeleteFalseAndUuid(parentUnitName, userMasterEntity, uuid)
						.orElse(null);

				if (parent == null) {
					throw new RuntimeException("Parent unit not found at row " + (rowIndex + 1));
				}

				entity.setParentUnit(parent);
			}
		}
		existingNames.add(nameEnglish);
		return entity;
	}

	private void deleteDbPlanningEntry(Long dbPlanningId) {
		databasePlanningService.deleteDbPlanningEntity(dbPlanningId);
	}

	private Map<String, List<List<String>>> formateRows(Map<String, List<List<String>>> excelData) {
		Map<String, List<List<String>>> masterMap = new HashMap<>();
		for (Map.Entry<String, List<List<String>>> key : excelData.entrySet()) {
			String keyString = key.getKey().trim().toLowerCase();
			if (TABLE_WITH_ALLOWED_EMPTY_CELLS.containsKey(keyString)) {
				List<List<String>> newMasterList = new ArrayList<>();
				List<List<String>> oldExcelList = key.getValue();
				newMasterList.add(oldExcelList.get(0));
				for (int i = 1; i < oldExcelList.size(); i++) {
					List<String> list = new ArrayList<>(oldExcelList.get(i));
					if (list.size() < TABLE_WITH_ALLOWED_EMPTY_CELLS.get(keyString)) {
						if (!LanguageUtils.containsGujarati(list)) {
							list.add(1, " ");
						}
						if (!LanguageUtils.containsHindi(list)) {
							list.add(2, " ");
						}
					}
					newMasterList.add(list);
				}
				masterMap.put(key.getKey(), newMasterList);
			} else {
				masterMap.put(key.getKey(), key.getValue());
			}
		}
		return masterMap;
	}

	@Transactional
	private void saveMenuItemRawMaterialEntity(List<List<String>> masterList, UserMasterEntity userMasterEntity,
			String uuid, Map<String, List<String>> errorMap) {

		if (masterList == null || masterList.size() <= 1) {
			return;
		}

		Map<String, RawMaterialMasterEntity> rawMaterialMap = rawMaterialMasterRepository.findByUuid(uuid).stream()
				.collect(Collectors.toMap(RawMaterialMasterEntity::getNameEnglish, Function.identity(),
						(existing, duplicate) -> {
							errorMap.computeIfAbsent("menu_item_raw_material", k -> new ArrayList<>())
									.add("Duplicate RawMaterial found: " + duplicate.getNameEnglish());
							return existing;
						}));

		Map<String, MenuItemMasterEntity> menuItemMap = menuItemMasterRepository.findByUuid(uuid).stream().collect(
				Collectors.toMap(MenuItemMasterEntity::getNameEnglish, Function.identity(), (existing, duplicate) -> {
					errorMap.computeIfAbsent("menu_item_raw_material", k -> new ArrayList<>())
							.add("Duplicate MenuItem found: " + duplicate.getNameEnglish());
					return existing;
				}));

		Map<String, UnitMasterEntity> unitMap = unitMasterRepository.findByUuid(uuid).stream().collect(
				Collectors.toMap(UnitMasterEntity::getNameEnglish, Function.identity(), (existing, duplicate) -> {
					errorMap.computeIfAbsent("menu_item_raw_material", k -> new ArrayList<>())
							.add("Duplicate Unit found: " + duplicate.getNameEnglish());
					return existing;
				}));

		processBulkUpload(
				masterList, "menu_item_raw_material", errorMap, i -> createMenuItemRawMaterialEntity(masterList.get(i),
						i, userMasterEntity, uuid, rawMaterialMap, menuItemMap, unitMap),
				menuItemRawMaterialRepository);
	}

	private MenuItemRawMaterialEntity createMenuItemRawMaterialEntity(List<String> list, Integer rowIndex,
			UserMasterEntity userMasterEntity, String uuid, Map<String, RawMaterialMasterEntity> rawMaterialMap,
			Map<String, MenuItemMasterEntity> menuItemMap, Map<String, UnitMasterEntity> unitMap) {

		MenuItemRawMaterialEntity entity = new MenuItemRawMaterialEntity();

		entity.setIsActive(true);
		entity.setIsDelete(false);
		entity.setUpdatedAt(LocalDateTime.now());
		entity.setUser(userMasterEntity);
		entity.setUuid(uuid);
		entity.setVenue("");
		RawMaterialMasterEntity rawMaterial = rawMaterialMap.get(list.get(1));

		if (rawMaterial == null) {
			throw new RuntimeException("Raw material not found at row " + (rowIndex + 1));
		}

		BigDecimal weight = new BigDecimal(list.get(2));

		UnitMasterEntity unit = unitMap.get(list.get(3));

		if (unit == null) {
			throw new RuntimeException("Unit not found at row " + (rowIndex + 1));
		}

		BigDecimal finalWeight = menuItemRawMaterialServiceImpl.adjustBaseWeight(weight, unit,
				rawMaterial.getUnit() == null ? unit : rawMaterial.getUnit());

		entity.setWeight(weight);
		BigDecimal rate = finalWeight.multiply(rawMaterial.getSupplierRate()).setScale(2, RoundingMode.HALF_UP);

		entity.setRate(rate);
		entity.setRawMaterial(rawMaterial);

		MenuItemMasterEntity menuItem = menuItemMap.get(list.get(0));

		if (menuItem == null) {
			throw new RuntimeException("Menu item not found at row " + (rowIndex + 1));
		}

		entity.setMenuItem(menuItem);

		entity.setUnit(unit);

		return entity;
	}

	private MenuItemMasterEntity getMenuItemMasterEntity(String categoryEnglish, String uuid) {
		return menuItemMasterRepository.findByNameEnglishAndUuid(categoryEnglish, uuid).orElse(null);
	}

	@Transactional
	private void saveMenuItemMasterEntity(List<List<String>> masterList, UserMasterEntity userMasterEntity, String uuid,
			Map<String, List<String>> errorMap, Long dbPlanningId) {

		Map<String, MenuCategoryMasterEntity> categoryMap = menuCategoryMasterRepository.findByUuid(uuid).stream()
				.collect(Collectors.toMap(MenuCategoryMasterEntity::getNameEnglish, Function.identity(),
						(existing, duplicate) -> {
							errorMap.computeIfAbsent("menuitems", k -> new ArrayList<>())
									.add("Duplicate Menu Category found: " + duplicate.getNameEnglish());
							return existing;
						}));

		Set<String> existingNames = menuItemMasterRepository.findByUuidAndUserAndIsDeleteFalse(uuid, userMasterEntity)
				.stream().map(MenuItemMasterEntity::getNameEnglish).collect(Collectors.toSet());

		processBulkUpload(masterList, "menuitems", errorMap, i -> createMenuItemMasterEntity(masterList.get(i), i,
				userMasterEntity, uuid, dbPlanningId, categoryMap, existingNames), menuItemMasterRepository);
	}

	private MenuItemMasterEntity createMenuItemMasterEntity(List<String> list, int counter,
			UserMasterEntity userMasterEntity, String uuid, Long dbPlanningId,
			Map<String, MenuCategoryMasterEntity> categoryMap, Set<String> existingNames) {

		MenuItemMasterEntity entity = new MenuItemMasterEntity();
		String nameEnglish = list.get(0);

		if (existingNames.contains(nameEnglish)) {
			throw new RuntimeException("Duplicate MenuItem: " + nameEnglish);
		}

		entity.setIsActive(true);
		entity.setIsDelete(false);
		entity.setNameEnglish(list.get(0));
		entity.setNameGujarati(list.get(1));
		entity.setNameHindi(list.get(2));
		entity.setPrice(BigDecimal.ZERO);
		entity.setSequence(counter);
		entity.setSlogan(list.get(3));
		entity.setUpdatedAt(LocalDateTime.now());

		MenuCategoryMasterEntity category = categoryMap.get(list.get(4));

		if (category == null) {
			throw new RuntimeException("Menu Category not found at row " + (counter + 1));
		}

		entity.setMenuCategory(category);
		entity.setMenuSubCategory(null);
		entity.setUser(userMasterEntity);
		entity.setUuid(uuid);

		entity.setImagePath(String.format(MEMU_ITEMS_IMAGE_PATH, userMasterEntity.getEmail(), entity.getNameEnglish()));
		existingNames.add(nameEnglish);
		return entity;
	}

	@Transactional
	private void saveMenuCategoryMasterEntity(List<List<String>> masterList, UserMasterEntity userMasterEntity,
			String uuid, Map<String, List<String>> errorMap, Long dbPlanningId) {

		Set<String> existingNames = menuCategoryMasterRepository
				.findByUuidAndUserAndIsDeleteFalse(uuid, userMasterEntity).stream()
				.map(MenuCategoryMasterEntity::getNameEnglish).collect(Collectors.toSet());

		processBulkUpload(masterList, "menucategory", errorMap, i -> createMenuCategoryMasterEntity(masterList.get(i),
				i, userMasterEntity, uuid, dbPlanningId, existingNames), menuCategoryMasterRepository);
	}

	private MenuCategoryMasterEntity createMenuCategoryMasterEntity(List<String> list, int counter,
			UserMasterEntity userMasterEntity, String uuid, Long dbPlanningId, Set<String> existingNames) {
		MenuCategoryMasterEntity entity = new MenuCategoryMasterEntity();
		String nameEnglish = list.get(0);

		if (existingNames.contains(nameEnglish)) {
			throw new RuntimeException("Duplicate Menu Category: " + nameEnglish);
		}

		// Duplicate check
		entity.setIsActive(true);
		entity.setIsDelete(false);
		entity.setNameEnglish(list.get(0));
		entity.setNameGujarati(list.get(1));
		entity.setNameHindi(list.get(2));
		entity.setMenuSlogan(list.get(3));
		entity.setSequence(counter);
		entity.setUpdatedAt(null);
		entity.setImagePath(
				String.format(MENU_CATEGORY_IMAGE_PATH, userMasterEntity.getEmail(), entity.getNameEnglish()));
		entity.setPrice(BigDecimal.ZERO);
		entity.setUser(userMasterEntity);
		entity.setUuid(uuid);
		existingNames.add(nameEnglish);
		return entity;
	}

	@Transactional
	private void saveRawMaterialMasterEntity(List<List<String>> masterList, UserMasterEntity userMasterEntity,
			String uuid, Map<String, List<String>> errorMap) {

		Map<String, RawMaterialCategoryMasterEntity> categoryMap = rawMaterialCategoryMasterRepository.findByUuid(uuid)
				.stream().collect(Collectors.toMap(RawMaterialCategoryMasterEntity::getNameEnglish, Function.identity(),
						(existing, duplicate) -> {
							errorMap.computeIfAbsent("rawmaterial", k -> new ArrayList<>())
									.add("Duplicate RawMaterial Category found: " + duplicate.getNameEnglish());
							return existing;
						}));

		Map<String, UnitMasterEntity> unitMap = unitMasterRepository.findByUuid(uuid).stream().collect(
				Collectors.toMap(UnitMasterEntity::getSymbolEnglish, Function.identity(), (existing, duplicate) -> {
					errorMap.computeIfAbsent("rawmaterial", k -> new ArrayList<>())
							.add("Duplicate Unit symbol found: " + duplicate.getSymbolEnglish());
					return existing;
				}));

		Set<String> existingNames = rawMaterialMasterRepository
				.findByUuidAndUserAndIsDeleteFalse(uuid, userMasterEntity).stream()
				.map(RawMaterialMasterEntity::getNameEnglish).collect(Collectors.toSet());

		processBulkUpload(masterList, "rawmaterial", errorMap, i -> createRawMaterialMasterEntity(masterList.get(i), i,
				userMasterEntity, uuid, categoryMap, unitMap, existingNames), rawMaterialMasterRepository);
	}

	private <T> void processBulkUpload(List<List<String>> masterList, String errorKey,
			Map<String, List<String>> errorMap, Function<Integer, T> rowProcessor, JpaRepository<T, ?> repository) {

		if (masterList == null || masterList.size() <= 1)
			return;

		int batchSize = 100;

		List<String> errorList = new ArrayList<>();
		List<T> batch = new ArrayList<>(batchSize);

		for (int i = 1; i < masterList.size(); i++) {
			try {
				T entity = rowProcessor.apply(i);

				if (entity != null) {
					batch.add(entity);
				}

				if (batch.size() == batchSize) {
					repository.saveAll(batch);
					repository.flush();
					batch.clear();
				}

			} catch (Exception e) {
				errorList.add(getFormattedErrorMessage(i, masterList.get(i), e));
			}
		}

		if (!batch.isEmpty()) {
			repository.saveAll(batch);
			repository.flush();
		}

		if (!errorList.isEmpty()) {
			errorMap.computeIfAbsent(errorKey, k -> new ArrayList<>()).addAll(errorList);
		}
	}

	private RawMaterialMasterEntity createRawMaterialMasterEntity(List<String> list, int counter,
			UserMasterEntity userMasterEntity, String uuid, Map<String, RawMaterialCategoryMasterEntity> categoryMap,
			Map<String, UnitMasterEntity> unitMap, Set<String> existingNames) {

		RawMaterialMasterEntity entity = new RawMaterialMasterEntity();
		String nameEnglish = list.get(0);

		if (existingNames.contains(nameEnglish)) {
			throw new RuntimeException("Duplicate Raw Material: " + nameEnglish);
		}

		entity.setIsGeneralFix(false);
		entity.setNameEnglish(list.get(0));
		entity.setNameGujarati(list.get(1));
		entity.setNameHindi(list.get(2));
		entity.setSupplierRate(new BigDecimal(list.get(3)));
		entity.setWeightPer100Pax(BigDecimal.ZERO);
		entity.setCreatedAt(LocalDateTime.now());
		entity.setSequence(counter);
		entity.setUpdatedAt(null);
		entity.setIsActive(true);
		entity.setIsDelete(false);
		entity.setUser(userMasterEntity);
		entity.setUuid(uuid);
		System.out.println("OPB: " + new BigDecimal(list.get(6)));
		entity.setOpbStock(new BigDecimal(list.get(6)));
		entity.setExpiryDate(LocalDate.now().plusMonths(1));
		entity.setMinStock(BigDecimal.ZERO);
		entity.setIsApplyCal(true);

		RawMaterialCategoryMasterEntity category = categoryMap.get(list.get(4));

		if (category == null) {
			throw new RuntimeException("Category not found at row " + (counter + 1));
		}

		entity.setRawMaterialCat(category);

		UnitMasterEntity unit = unitMap.get(list.get(5));

		if (unit == null) {
			throw new RuntimeException("Unit not found at row " + (counter + 1));
		}

		entity.setUnit(unit);
		existingNames.add(nameEnglish);
		return entity;
	}

	@Transactional
	private void saveRawMaterialCategoryMasterEntity(List<List<String>> masterList, UserMasterEntity userMasterEntity,
			String uuid, Map<String, List<String>> errorMap) {

		RawMaterialCategoryTypeMasterEntity defaultType = getRawMaterialCategoryTypeMasterEntity(1L);

		Set<String> existingNames = rawMaterialCategoryMasterRepository
				.findByUuidAndUserAndIsDeleteFalse(uuid, userMasterEntity).stream()
				.map(RawMaterialCategoryMasterEntity::getNameEnglish).collect(Collectors.toSet());

		processBulkUpload(masterList, "raw_material_category", errorMap,
				i -> createRawMaterialCategoryMasterEntityObject(masterList.get(i), i, userMasterEntity, uuid,
						defaultType, existingNames),
				rawMaterialCategoryMasterRepository);
	}

	private RawMaterialCategoryMasterEntity createRawMaterialCategoryMasterEntityObject(List<String> list, int counter,
			UserMasterEntity userMasterEntity, String uuid, RawMaterialCategoryTypeMasterEntity defaultType,
			Set<String> existingNames) {

		RawMaterialCategoryMasterEntity entity = new RawMaterialCategoryMasterEntity();

		String nameEnglish = list.get(0);

		// Duplicate check
		if (existingNames.contains(nameEnglish)) {
			throw new RuntimeException("Duplicate Raw Material Category : " + nameEnglish);
		}
		entity.setIsActive(true);
		entity.setIsDelete(false);
		entity.setNameEnglish(list.get(0));
		entity.setNameGujarati(list.get(1));
		entity.setNameHindi(list.get(2));
		entity.setSequence(counter);
		entity.setUpdatedAt(null);

		entity.setRawMaterialCatType(defaultType);

		entity.setUser(userMasterEntity);
		entity.setIsDirect(false);
		entity.setUuid(uuid);
		existingNames.add(nameEnglish);
		return entity;
	}

	private RawMaterialCategoryTypeMasterEntity getRawMaterialCategoryTypeMasterEntity(Long id) {
		return rawMaterialCategoryTypeMasterRepository.findByIdAndIsDeleteFalse(id);
	}

	private UserMasterEntity getUserMasterEntity(Long id) {
		return userMasterRepository.findByIdAndIsDeleteFalse(id).orElse(null);
	}

	private UnitMasterEntity getUnitMasterEntity(String symbolEnglish, String uuid) {
		List<UnitMasterEntity> unitList = unitMasterRepository.findBySymbolEnglishAndUuidAndIsDeleteFalse(symbolEnglish,
				uuid);
		return unitList.isEmpty() ? null : unitList.get(0);
	}

	private RawMaterialCategoryMasterEntity getRawMaterialCategoryMasterEntity(String categoryEnglish, String uuid) {
		return rawMaterialCategoryMasterRepository.findByNameEnglishAndUuidAndIsDeleteFalse(categoryEnglish, uuid)
				.orElse(null);
	}

	private MenuCategoryMasterEntity getMenuCategoryMasterEntity(String categoryNameEnglish, String uuid) {
		return menuCategoryMasterRepository.findByNameEnglishAndUuidAndIsDeleteFalse(categoryNameEnglish, uuid)
				.orElse(null);
	}

	private RawMaterialMasterEntity getRawMaterialMasterEntity(String categoryEnglish, String uuid) {
		return rawMaterialMasterRepository.findByNameEnglishAndUuidAndIsDeleteFalse(categoryEnglish, uuid).orElse(null);
	}

	private static String getFormattedErrorMessage(int rowNumber, List<String> rowData, Exception e) {
		return String.format("Error at row %d for data %s : %s", rowNumber + 1, Arrays.toString(rowData.toArray()),
				e.getMessage());
	}
}
