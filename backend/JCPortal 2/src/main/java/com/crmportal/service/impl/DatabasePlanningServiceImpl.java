package com.crmportal.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.CrockeryCutleryEntity;
import com.crmportal.entity.DatabasePlanningEntity;
import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.MenuItemRawMaterialEntity;
import com.crmportal.entity.MenuItemRawMaterialRateDishCostingEntity;
import com.crmportal.entity.RawMaterialCategoryMasterEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UnitRangeEntity;
import com.crmportal.entity.UnitStepwiseRangeEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.RawMaterialCategoryMasterMapper;
import com.crmportal.mapper.UnitMasterMapper;
import com.crmportal.repository.CrockeryCutleryRepository;
import com.crmportal.repository.DatabasePlanningEntityRepository;
import com.crmportal.repository.MenuCategoryMasterRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.MenuItemRawMaterialRateDishCostingRepository;
import com.crmportal.repository.MenuItemRawMaterialRepository;
import com.crmportal.repository.RawMaterialCategoryMasterRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.repository.UnitRangeRepository;
import com.crmportal.repository.UnitStepwiseRangeRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.AssignDbRequest;
import com.crmportal.request.dto.DBExcelRequestDto;
import com.crmportal.service.DatabasePlanningService;
import com.crmportal.utility.ResponseUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class DatabasePlanningServiceImpl implements DatabasePlanningService {

	@Autowired
	private DatabasePlanningEntityRepository databasePlanningEntityRepository;
	@Autowired
	private UserMasterRepository userMasterRepository;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private RawMaterialCategoryMasterRepository rawMaterialCategoryMasterRepository;
	@Autowired
	private RawMaterialMasterRepository rawMaterialMasterRepository;
	@Autowired
	private MenuCategoryMasterRepository menuCategoryMasterRepository;
	@Autowired
	private MenuItemMasterRepository menuItemMasterRepository;
	@Autowired
	private MenuItemRawMaterialRepository menuItemRawMaterialRepository;
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private RawMaterialCategoryMasterMapper rawMaterialCategoryMasterMapper;
	@Autowired
	private UnitMasterMapper unitMasterMapper;
	@Autowired
	private UnitMasterRepository unitMasterRepository;
	@Autowired
	private MenuItemRawMaterialRateDishCostingRepository costingRepository;
	@Autowired
	UnitRangeRepository unitRangeRepository;
	@Autowired
	UnitStepwiseRangeRepository stepwiseRangeRepository;
	@Autowired
	CrockeryCutleryRepository crockeryCutleryRepository;

	@Override
	public Long saveDatabasePlanningEntity(DBExcelRequestDto dbExcelRequestDto, String uuid,
			Map<String, List<String>> errorMap) {
		Long db_planning_id = 0L;
		try {
			DatabasePlanningEntity databasePlanningEntity = new DatabasePlanningEntity();
			databasePlanningEntity.setDbName(dbExcelRequestDto.getDbName());
			databasePlanningEntity.setInstructions(dbExcelRequestDto.getInstructions());
			databasePlanningEntity.setUser(getUserMasterEntity(Long.parseLong(dbExcelRequestDto.getUserId())));
			databasePlanningEntity.setUuid(uuid);
			databasePlanningEntity.setState(dbExcelRequestDto.getState());
			DatabasePlanningEntity save = databasePlanningEntityRepository.save(databasePlanningEntity);
			db_planning_id = save.getId();
		} catch (Exception e) {
			e.printStackTrace();
			errorMap.put("", Collections.singletonList(e.getLocalizedMessage()));
		}
		return db_planning_id;
	}

	@Override
	public Map<String, Object> getAllDatabasePlanningEntities() {
		try {
			List<DatabasePlanningEntity> list = databasePlanningEntityRepository.findAll().stream().filter(
					entity -> Objects.nonNull(entity) && Objects.nonNull(entity.getUser()) && entity.getIsPublished())
					.collect(Collectors.toList());
			return ResponseUtils.createSuccessRespones(createJsonNodeArray(list), "Data fetched successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.createFailedRespones(e.getLocalizedMessage());
		}
	}

	private JsonNode createJsonNodeArray(List<DatabasePlanningEntity> list) {
		ArrayNode arrayNode = objectMapper.createArrayNode();
		for (DatabasePlanningEntity databasePlanningEntity : list) {
			ObjectNode node = objectMapper.createObjectNode();

			node.put("db_planning_id", databasePlanningEntity.getId());
			node.put("dbName", databasePlanningEntity.getDbName());
			node.put("createdAt", databasePlanningEntity.getCreatedAt().toString());
			node.put("uuid", databasePlanningEntity.getUuid());
			node.put("instructions", databasePlanningEntity.getInstructions());
			node.put("user", databasePlanningEntity.getUser().getUserCode());
			node.put("state", databasePlanningEntity.getState());
			arrayNode.add(node);
		}
		return arrayNode;
	}

	private UserMasterEntity getUserMasterEntity(Long id) {
		return userMasterRepository.findById(id).orElse(null);
	}

	@Override
	public Map<String, Object> getByDbPlanningIdAndDbName(String dbPlanningId) {
		DatabasePlanningEntity databasePlanningEntity = databasePlanningEntityRepository
				.findById(Long.parseLong(dbPlanningId)).orElse(null);
		if (Objects.nonNull(databasePlanningEntity)) {
			return ResponseUtils.createSuccessRespones(
					createJsonNodeArray(Collections.singletonList(databasePlanningEntity)),
					"Data Fetched Successfully");
		}
		return ResponseUtils.createFailedRespones("Data not found for dbPlanningId " + dbPlanningId);
	}

	@Override
	public Map<String, Object> assignDbToUser(AssignDbRequest assignDbRequest) {
		try {
			DatabasePlanningEntity databasePlanningEntity = databasePlanningEntityRepository
					.findById(Long.parseLong(assignDbRequest.getDbPlanningId())).orElseGet(null);
			UserMasterEntity userMasterEntity = getUserMasterEntity(Long.parseLong(assignDbRequest.getUserId()));
			if (Objects.isNull(databasePlanningEntity)) {
				return ResponseUtils.createFailedRespones("Data not found",
						"Data not found for db_planning_id " + assignDbRequest.getDbPlanningId());
			} else if (Objects.isNull(userMasterEntity)) {
				return ResponseUtils.createFailedRespones("User not found",
						"User not found for userId " + assignDbRequest.getUserId());
			}

			DatabasePlanningEntity userDb = databasePlanningEntityRepository.findByUser(userMasterEntity).orElse(null);
			String newUuid = UUID.randomUUID().toString();
			if (userDb != null) {
				newUuid = userDb.getUuid();
			}
			objectMapper.registerModule(new JavaTimeModule());
			saveUnitMasterEntity(newUuid, userMasterEntity, databasePlanningEntity.getUuid());
			saveUnitRangeMasterEntity(newUuid, userMasterEntity, databasePlanningEntity.getUuid());
			saveUnitStepsMasterEntity(newUuid, userMasterEntity, databasePlanningEntity.getUuid());
			saveRawMaterialCategoryMasterEntity(newUuid, userMasterEntity, databasePlanningEntity.getUuid());
			saveRawMaterialMasterEntity(newUuid, userMasterEntity, databasePlanningEntity.getUuid());
			saveMenuCategoryMasterEntity(newUuid, userMasterEntity, databasePlanningEntity.getUuid());
			saveMenuItemMasterEntity(newUuid, userMasterEntity, databasePlanningEntity.getUuid());
			saveMenuItemRawMaterialEntity(newUuid, userMasterEntity, databasePlanningEntity.getUuid());
			saveMenuItemRawMaterialRateDishCostingEntity(newUuid, userMasterEntity, databasePlanningEntity.getUuid());
			if (userDb == null) {
				saveOrUpdateDatabasePlanningEntity(databasePlanningEntity, userMasterEntity, newUuid,
						assignDbRequest.getDbPlanningId());
			}
			return ResponseUtils.createSuccessRespones("Assignment Successfull",
					"Db Assignment successfull for newUUID " + newUuid);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.createFailedRespones(e.getLocalizedMessage());
		}
	}

	@Transactional
	private void saveUnitStepsMasterEntity(String newUuid, UserMasterEntity userMasterEntity, String oldUuid) {

		System.err.println("saveUnitStepsMasterEntity ------ in ");

		// Query 1: Get old stepwise ranges
		List<UnitStepwiseRangeEntity> oldEntities = stepwiseRangeRepository.findAllByUuidAndIsDeleteFalse(oldUuid);

		if (oldEntities.isEmpty()) {
			return;
		}

		// Query 2: Get all active units for new UUID
		Map<String, UnitMasterEntity> unitMap = unitMasterRepository.findByUuidAndIsDeleteFalse(newUuid).stream()
				.collect(Collectors.toMap(UnitMasterEntity::getNameEnglish, u -> u,
						(existing, replacement) -> existing));

		// Query 3: Get existing stepwise ranges for new UUID
		List<UnitStepwiseRangeEntity> existingEntities = stepwiseRangeRepository.findAllByUuidAndIsDeleteFalse(newUuid);

		// Create lookup keys for duplicate checking
		Set<String> existingStepKeys = existingEntities.stream().filter(entity -> entity.getUnit() != null)
				.map(entity -> entity.getUnit().getNameEnglish() + "_" + entity.getStepValue())
				.collect(Collectors.toSet());

		List<UnitStepwiseRangeEntity> newEntities = oldEntities.stream().filter(old -> old.getUnit() != null)
				.filter(old -> {

					String key = old.getUnit().getNameEnglish() + "_" + old.getStepValue();

					// Skip if already exists
					return !existingStepKeys.contains(key);
				}).map(old -> {

					UnitMasterEntity mappedUnit = unitMap.get(old.getUnit().getNameEnglish());

					// Unit was not cloned / does not exist
					if (mappedUnit == null) {
						return null;
					}

					UnitStepwiseRangeEntity clone = new UnitStepwiseRangeEntity();

					clone.setIsActive(old.getIsActive());
					clone.setIsDelete(false);
					clone.setIsPublished(false);
					clone.setStepValue(old.getStepValue());
					clone.setUuid(newUuid);
					clone.setUnit(mappedUnit);

					return clone;
				}).filter(Objects::nonNull).collect(Collectors.toList());

		// Batch insert only new records
		if (!newEntities.isEmpty()) {
			stepwiseRangeRepository.saveAll(newEntities);
		}

		System.err.println("saveUnitStepsMasterEntity ------ out ");

	}

	@Transactional
	private void saveUnitRangeMasterEntity(String newUuid, UserMasterEntity userMasterEntity, String oldUuid) {

		System.err.println("saveUnitRangeMasterEntity ------ in ");

		// Query 1: Get old ranges
		List<UnitRangeEntity> oldEntities = unitRangeRepository.findAllByUuidAndIsDeleteFalse(oldUuid);

		if (oldEntities.isEmpty()) {
			return;
		}

		// Query 2: Get all units for new UUID
		Map<String, UnitMasterEntity> unitMap = unitMasterRepository.findByUuid(newUuid).stream()
				.collect(Collectors.toMap(UnitMasterEntity::getNameEnglish, u -> u));

		// Query 3: Get existing ranges for new UUID
		List<UnitRangeEntity> existingEntities = unitRangeRepository.findAllByUuidAndIsDeleteFalse(newUuid);

		// Create unique keys for existing ranges
		Set<String> existingRangeKeys = existingEntities.stream().filter(entity -> entity.getUnit() != null)
				.map(entity -> entity.getUnit().getNameEnglish() + "_" + entity.getRangeType())
				.collect(Collectors.toSet());

		List<UnitRangeEntity> newEntities = oldEntities.stream().filter(old -> old.getUnit() != null).filter(old -> {

			String key = old.getUnit().getNameEnglish() + "_" + old.getRangeType();

			// Skip if already exists
			return !existingRangeKeys.contains(key);
		}).map(old -> {

			UnitMasterEntity mappedUnit = unitMap.get(old.getUnit().getNameEnglish());

			// Skip if corresponding unit does not exist
			if (mappedUnit == null) {
				return null;
			}

			UnitRangeEntity clone = new UnitRangeEntity();

			clone.setIsActive(old.getIsActive());
			clone.setIsDelete(false);
			clone.setIsPublished(false);
			clone.setMaxValue(old.getMaxValue());
			clone.setMinValue(old.getMinValue());
			clone.setRangeType(old.getRangeType());
			clone.setRoundOffValue(old.getRoundOffValue());
			clone.setUuid(newUuid);
			clone.setUnit(mappedUnit);

			return clone;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		// Batch insert only new records
		if (!newEntities.isEmpty()) {
			unitRangeRepository.saveAll(newEntities);
		}

		System.err.println("saveUnitRangeMasterEntity ------ out ");

	}

	@Transactional
	private void saveMenuItemRawMaterialRateDishCostingEntity(String newUuid, UserMasterEntity userMasterEntity,
			String oldUuid) {

		System.err.println("saveMenuItemRawMaterialRateDishCostingEntity ------ in ");

		// Query 1: Get old costing records
		List<MenuItemRawMaterialRateDishCostingEntity> oldEntities = costingRepository
				.findByUuidAndIsDeleteFalse(oldUuid);

		if (oldEntities == null || oldEntities.isEmpty()) {
			return;
		}

		// Query 2: Get Menu Items for new UUID
		Map<String, MenuItemMasterEntity> menuItemMap = menuItemMasterRepository.findByUuidAndIsDeleteFalse(newUuid)
				.stream().collect(Collectors.toMap(MenuItemMasterEntity::getNameEnglish, menuItem -> menuItem,
						(existing, replacement) -> existing));

		// Query 3: Get existing costing records for new UUID
		Set<String> existingMenuNames = costingRepository.findByUuidAndIsDeleteFalse(newUuid).stream()
				.filter(entity -> entity.getMenuItem() != null).map(entity -> entity.getMenuItem().getNameEnglish())
				.filter(Objects::nonNull).collect(Collectors.toSet());

		final LocalDateTime now = LocalDateTime.now();

		List<MenuItemRawMaterialRateDishCostingEntity> newEntities = new ArrayList<>();

		for (MenuItemRawMaterialRateDishCostingEntity old : oldEntities) {

			// Validate menu item
			if (old.getMenuItem() == null || old.getMenuItem().getNameEnglish() == null) {
				continue;
			}

			String menuItemName = old.getMenuItem().getNameEnglish();

			// Skip if already exists
			if (existingMenuNames.contains(menuItemName)) {
				continue;
			}

			// Get mapped MenuItem from new UUID
			MenuItemMasterEntity menuItem = menuItemMap.get(menuItemName);

			// Skip if corresponding menu item does not exist
			if (menuItem == null) {
				continue;
			}

			// Create fresh entity
			MenuItemRawMaterialRateDishCostingEntity clone = new MenuItemRawMaterialRateDishCostingEntity();

			// =========================
			// Copy fields
			// =========================

			clone.setTotalRate(old.getTotalRate());
			clone.setDishCosting(old.getDishCosting());
			clone.setIsActive(old.getIsActive());

			// =========================
			// New entity values
			// =========================

			clone.setIsDelete(false);
			clone.setIsPublished(false);
			clone.setUuid(newUuid);
			clone.setUser(userMasterEntity);
			clone.setUpdatedAt(now);

			// createdAt automatically handled by @CreationTimestamp

			// =========================
			// Map relationship
			// =========================

			clone.setMenuItem(menuItem);

			newEntities.add(clone);

			// Prevent duplicates within current batch
			existingMenuNames.add(menuItemName);
		}

		// Batch insert
		if (!newEntities.isEmpty()) {
			costingRepository.saveAll(newEntities);
		}

		System.err.println("saveMenuItemRawMaterialRateDishCostingEntity ------ out ");

	}

	@Transactional
	private void saveUnitMasterEntity(String newUuid, UserMasterEntity userMasterEntity, String oldUuid) {
		System.err.println("saveUnitMasterEntity ------ in ");
		List<UnitMasterEntity> oldUnits = unitMasterRepository.findByUuidAndIsDeleteFalse(oldUuid);

		if (oldUnits.isEmpty()) {
			return;
		}

		Set<String> existingNames = new HashSet<>(unitMasterRepository.findAllActiveNamesByUser(userMasterEntity));

		Map<Long, UnitMasterEntity> cloneMap = new HashMap<>();

		for (UnitMasterEntity old : oldUnits) {

			if (existingNames.contains(old.getNameEnglish())) {
				continue;
			}

			UnitMasterEntity clone = new UnitMasterEntity();

			clone.setNameEnglish(old.getNameEnglish());
			clone.setSymbolEnglish(old.getSymbolEnglish());
			clone.setNameHindi(old.getNameHindi());
			clone.setNameGujarati(old.getNameGujarati());
			clone.setSymbolHindi(old.getSymbolHindi());
			clone.setSymbolGujarati(old.getSymbolGujarati());

			clone.setIsParentUnit(old.getIsParentUnit());
			clone.setEquivalentValue(old.getEquivalentValue());
			clone.setDecimalLimit(old.getDecimalLimit());
			clone.setRangeType(old.getRangeType());

			clone.setUuid(newUuid);
			clone.setUser(userMasterEntity);
			clone.setIsPublished(false);
			clone.setIsDelete(false);
			clone.setIsActive(true);

			cloneMap.put(old.getId(), clone);
		}

		for (UnitMasterEntity old : oldUnits) {
			UnitMasterEntity clone = cloneMap.get(old.getId());
			if (clone == null) {
				continue;
			}
			if (old.getParentUnit() != null) {
				UnitMasterEntity parentClone = cloneMap.get(old.getParentUnit().getId());
				if (parentClone != null) {
					clone.setParentUnit(parentClone);
				}
			}
		}

		if (!cloneMap.isEmpty()) {
			unitMasterRepository.saveAll(cloneMap.values());
		}

		System.err.println("saveUnitMasterEntity ------ out ");
	}

	@Transactional
	private void saveOrUpdateDatabasePlanningEntity(DatabasePlanningEntity databasePlanningEntity,
			UserMasterEntity userMasterEntity, String newUuid, String dbPlanningId) {
		DatabasePlanningEntity entity = databasePlanningEntityRepository.findByUser(userMasterEntity).orElse(null);
		if (Objects.isNull(entity)) {
			entity = new DatabasePlanningEntity();
			entity.setDbName(databasePlanningEntity.getDbName() + "_" + userMasterEntity.getContactNo());
			entity.setIsPublished(false);
			entity.setUser(userMasterEntity);
			entity.setUuid(newUuid);
			entity.setIsActive(true);
			entity.setIsDelete(false);
		} else {
			entity.setDbName(databasePlanningEntity.getDbName());
		}
		entity.setState(databasePlanningEntity.getState());
		entity.setInstructions(databasePlanningEntity.getInstructions());
		entity.setParentDbId(Long.parseLong(dbPlanningId));

		databasePlanningEntityRepository.save(entity);
	}

	@Transactional
	private void saveRawMaterialCategoryMasterEntity(String newUuid, UserMasterEntity userMasterEntity,
			String oldUuid) {

		System.err.println("saveRawMaterialCategoryMasterEntity ------ in ");

		// Query 1: Fetch old categories
		List<RawMaterialCategoryMasterEntity> oldEntities = rawMaterialCategoryMasterRepository
				.findByUuidAndIsDeleteFalse(oldUuid);

		if (oldEntities.isEmpty()) {
			return;
		}

		// Query 2: Fetch existing categories for new UUID
		List<RawMaterialCategoryMasterEntity> existingEntities = rawMaterialCategoryMasterRepository
				.findByUuidAndIsDeleteFalse(newUuid);

		// Create a Set of existing category names
		Set<String> existingNames = existingEntities.stream().map(RawMaterialCategoryMasterEntity::getNameEnglish)
				.filter(Objects::nonNull).collect(Collectors.toSet());

		// Clone only categories that don't already exist
		List<RawMaterialCategoryMasterEntity> newEntities = oldEntities.stream().filter(
				oldEntity -> oldEntity.getNameEnglish() != null && !existingNames.contains(oldEntity.getNameEnglish()))
				.map(oldEntity -> {

					RawMaterialCategoryMasterEntity cloned = rawMaterialCategoryMasterMapper.cloneForNew(oldEntity,
							newUuid, userMasterEntity);

					cloned.setRawMaterialCatType(oldEntity.getRawMaterialCatType());

					return cloned;
				}).collect(Collectors.toList());

		// Save only if new records exist
		if (!newEntities.isEmpty()) {
			rawMaterialCategoryMasterRepository.saveAll(newEntities);
		}

		System.err.println("saveRawMaterialCategoryMasterEntity ------ out ");
	}

	@Transactional
	private void saveRawMaterialMasterEntity(String newUuid, UserMasterEntity userMasterEntity, String oldUuid) {
		System.err.println("saveRawMaterialMasterEntity ------ in ");
		List<RawMaterialMasterEntity> oldEntities = rawMaterialMasterRepository.findByUuidAndIsDeleteFalse(oldUuid);

		if (oldEntities.isEmpty()) {
			return;
		}

		Map<String, RawMaterialCategoryMasterEntity> categoryMap = rawMaterialCategoryMasterRepository
				.findByUuidAndIsDeleteFalse(newUuid).stream().collect(Collectors.toMap(
						RawMaterialCategoryMasterEntity::getNameEnglish, c -> c, (existing, replacement) -> existing));

		Map<String, UnitMasterEntity> unitMap = unitMasterRepository.findByUuidAndIsDeleteFalse(newUuid).stream()
				.collect(Collectors.toMap(UnitMasterEntity::getNameEnglish, u -> u,
						(existing, replacement) -> existing));

		Set<String> existingNames = rawMaterialMasterRepository.findByUuidAndIsDeleteFalse(newUuid).stream()
				.map(RawMaterialMasterEntity::getNameEnglish).filter(Objects::nonNull).collect(Collectors.toSet());

		final LocalDateTime now = LocalDateTime.now();
		final LocalDate expiryDate = LocalDate.now().plusMonths(1);
		List<RawMaterialMasterEntity> newEntities = new ArrayList<>();

		for (RawMaterialMasterEntity old : oldEntities) {
			if (old.getNameEnglish() == null || existingNames.contains(old.getNameEnglish())) {
				continue;
			}
			RawMaterialCategoryMasterEntity category = null;
			if (old.getRawMaterialCat() != null) {
				category = categoryMap.get(old.getRawMaterialCat().getNameEnglish());
			}
			UnitMasterEntity unit = null;
			if (old.getUnit() != null) {
				unit = unitMap.get(old.getUnit().getNameEnglish());
			}
			if (category == null || unit == null) {
				continue;
			}
			RawMaterialMasterEntity clone = new RawMaterialMasterEntity();

			// Names
			clone.setNameEnglish(old.getNameEnglish());
			clone.setNameHindi(old.getNameHindi());
			clone.setNameGujarati(old.getNameGujarati());

			// Unit - mapped from new UUID
			clone.setUnit(unit);

			// Pricing & consumption
			clone.setSupplierRate(old.getSupplierRate());
			clone.setDailyConsumption(old.getDailyConsumption());
			clone.setWeightPer100Pax(old.getWeightPer100Pax());

			// General settings
			clone.setIsGeneralFix(old.getIsGeneralFix());
			clone.setSequence(old.getSequence());

			// Status
			clone.setIsPublished(false);
			clone.setIsDelete(false);
			clone.setIsActive(old.getIsActive());

			// File
			clone.setFile(old.getFile());

			// User & Category
			clone.setUser(userMasterEntity);
			clone.setRawMaterialCat(category);

			// Stock
			clone.setOpbStock(old.getOpbStock());
			clone.setMinStock(old.getMinStock());

			// Dates
			clone.setCreatedAt(now);
			clone.setUpdatedAt(now);

			// Keep old expiry date OR use new expiry date
			clone.setExpiryDate(old.getExpiryDate() != null ? old.getExpiryDate() : expiryDate);

			// UUID
			clone.setUuid(newUuid);

			// Calculation settings
			clone.setIsApplyCal(old.getIsApplyCal());

			// Taxes
			clone.setCgst(old.getCgst());
			clone.setSgst(old.getSgst());
			clone.setIgst(old.getIgst());
			clone.setCess(old.getCess());

			newEntities.add(clone);
		}
		if (newEntities.isEmpty()) {
			System.err.println("No new raw materials to save");
			return;
		}
		List<RawMaterialMasterEntity> savedEntities = rawMaterialMasterRepository.saveAll(newEntities);

		List<CrockeryCutleryEntity> crockeryList = new ArrayList<>();
		for (RawMaterialMasterEntity entity : savedEntities) {
			if (entity.getRawMaterialCat() != null && entity.getRawMaterialCat().getRawMaterialCatType() != null
					&& Long.valueOf(2L).equals(entity.getRawMaterialCat().getRawMaterialCatType().getId())) {
				CrockeryCutleryEntity crockery = new CrockeryCutleryEntity();
				crockery.setRawMaterial(entity);
				crockery.setUser(userMasterEntity);
				crockery.setRawMaterialNameEnglish(entity.getNameEnglish());
				crockery.setRawMaterialNameHindi(entity.getNameHindi());
				crockery.setRawMaterialNameGujarati(entity.getNameGujarati());
				crockery.setRawMaterialCategory(entity.getRawMaterialCat());
				crockeryList.add(crockery);
			}
		}
		if (!crockeryList.isEmpty()) {
			crockeryCutleryRepository.saveAll(crockeryList);
		}

		System.err.println("saveRawMaterialMasterEntity ------ out ");
	}

	@Transactional
	private void saveMenuCategoryMasterEntity(String newUuid, UserMasterEntity userMasterEntity, String oldUuid) {

		System.err.println("saveMenuCategoryMasterEntity ------ in ");

		// Query 1: Get old menu categories
		List<MenuCategoryMasterEntity> oldEntities = menuCategoryMasterRepository.findByUuidAndIsDeleteFalse(oldUuid);

		if (oldEntities == null || oldEntities.isEmpty()) {
			return;
		}

		// Query 2: Get existing menu category names for new UUID
		Set<String> existingNames = menuCategoryMasterRepository.findByUuidAndIsDeleteFalse(newUuid).stream()
				.map(MenuCategoryMasterEntity::getNameEnglish).filter(Objects::nonNull).collect(Collectors.toSet());

		final LocalDateTime now = LocalDateTime.now();

		List<MenuCategoryMasterEntity> newEntities = new ArrayList<>();

		for (MenuCategoryMasterEntity old : oldEntities) {

			// Skip if already exists
			if (old.getNameEnglish() == null || existingNames.contains(old.getNameEnglish())) {
				continue;
			}

			// Create fresh entity
			MenuCategoryMasterEntity clone = new MenuCategoryMasterEntity();

			// Names
			clone.setNameEnglish(old.getNameEnglish());
			clone.setNameHindi(old.getNameHindi());
			clone.setNameGujarati(old.getNameGujarati());

			// Report names
			clone.setReportNameEnglish(old.getReportNameEnglish());
			clone.setReportNameHindi(old.getReportNameHindi());
			clone.setReportNameGujarati(old.getReportNameGujarati());

			// Menu details
			clone.setMenuSlogan(old.getMenuSlogan());
			clone.setPrice(old.getPrice());
			clone.setSequence(old.getSequence());

			// Status
			clone.setIsActive(old.getIsActive());
			clone.setIsDelete(false);
			clone.setIsPublished(false);

			// New user and UUID
			clone.setUser(userMasterEntity);
			clone.setUuid(newUuid);

			// Image path
			clone.setImagePath(
					"/jcupload/" + userMasterEntity.getEmail() + "/MENUCATEGORY/" + old.getNameEnglish() + ".jpg");

			// Dates
			// createdAt is automatically handled by @CreationTimestamp
			clone.setUpdatedAt(now);

			newEntities.add(clone);
		}

		// Batch insert
		if (!newEntities.isEmpty()) {
			menuCategoryMasterRepository.saveAll(newEntities);
		}

		System.err.println("saveMenuCategoryMasterEntity ------ out ");

	}

	@Transactional
	private void saveMenuItemMasterEntity(String newUuid, UserMasterEntity userMasterEntity, String oldUuid) {

		System.err.println("saveMenuItemMasterEntity ------ in ");

		// Query 1: Fetch old menu items
		List<MenuItemMasterEntity> oldEntities = menuItemMasterRepository.findByUuidAndIsDeleteFalse(oldUuid);

		if (oldEntities == null || oldEntities.isEmpty()) {
			return;
		}

		// Query 2: Fetch new UUID categories
		Map<String, MenuCategoryMasterEntity> categoryMap = menuCategoryMasterRepository
				.findByUuidAndIsDeleteFalse(newUuid).stream()
				.collect(Collectors.toMap(MenuCategoryMasterEntity::getNameEnglish, category -> category,
						(existing, replacement) -> existing));

		// Query 3: Fetch existing menu item names
		Set<String> existingNames = menuItemMasterRepository.findByUuidAndIsDeleteFalse(newUuid).stream()
				.map(MenuItemMasterEntity::getNameEnglish).filter(Objects::nonNull).collect(Collectors.toSet());

		final LocalDateTime now = LocalDateTime.now();

		List<MenuItemMasterEntity> newEntities = new ArrayList<>();

		for (MenuItemMasterEntity old : oldEntities) {

			// Skip if name already exists
			if (old.getNameEnglish() == null || existingNames.contains(old.getNameEnglish())) {
				continue;
			}

			// Map category from old UUID to new UUID
			MenuCategoryMasterEntity category = null;

			if (old.getMenuCategory() != null) {
				category = categoryMap.get(old.getMenuCategory().getNameEnglish());
			}

			/*
			 * If category exists in old record but was not cloned, skip this menu item.
			 */
			if (old.getMenuCategory() != null && category == null) {
				continue;
			}

			// Create fresh entity
			MenuItemMasterEntity clone = new MenuItemMasterEntity();

			// Names
			clone.setNameEnglish(old.getNameEnglish());
			clone.setNameHindi(old.getNameHindi());
			clone.setNameGujarati(old.getNameGujarati());

			// Instructions
			clone.setInstructionEnglish(old.getInstructionEnglish());
			clone.setInstructionHindi(old.getInstructionHindi());
			clone.setInstructionGujarati(old.getInstructionGujarati());

			// Status
			clone.setIsActive(old.getIsActive());
			clone.setIsDelete(false);
			clone.setIsPublished(false);

			// Menu details
			clone.setSlogan(old.getSlogan());
			clone.setPrice(old.getPrice());
			clone.setSequence(old.getSequence());

			// Image
			clone.setImagePath(
					"/jcupload/" + userMasterEntity.getEmail() + "/MENUITEM/" + old.getNameEnglish() + ".jpg");

			// Relations
			clone.setMenuCategory(category);

			/*
			 * MenuSubCategory is NOT mapped here yet. If you clone subcategories
			 * separately, create a map exactly like categoryMap and map it here.
			 */
			clone.setMenuSubCategory(null);

			// Other fields
			clone.setUrl(old.getUrl());
			clone.setRemarks(old.getRemarks());

			// User & UUID
			clone.setUser(userMasterEntity);
			clone.setUuid(newUuid);

			// Dates
			// createdAt is automatically generated by @CreationTimestamp
			clone.setUpdatedAt(now);

			newEntities.add(clone);
		}

		// Batch insert
		if (!newEntities.isEmpty()) {
			menuItemMasterRepository.saveAll(newEntities);
		}

		System.err.println("saveMenuItemMasterEntity ------ out ");

	}

	@Transactional
	private void saveMenuItemRawMaterialEntity(String newUuid, UserMasterEntity userMasterEntity, String oldUuid) {

		System.err.println("saveMenuItemRawMaterialEntity ------ in ");

		// Query 1: Get old records
		List<MenuItemRawMaterialEntity> oldEntities = menuItemRawMaterialRepository.findByUuidAndIsDeleteFalse(oldUuid);

		if (oldEntities == null || oldEntities.isEmpty()) {
			return;
		}

		// Query 2: Get Raw Materials for new UUID
		Map<String, RawMaterialMasterEntity> rawMaterialMap = rawMaterialMasterRepository
				.findByUuidAndIsDeleteFalse(newUuid).stream()
				.collect(Collectors.toMap(RawMaterialMasterEntity::getNameEnglish, rawMaterial -> rawMaterial,
						(existing, replacement) -> existing));

		// Query 3: Get Menu Items for new UUID
		Map<String, MenuItemMasterEntity> menuItemMap = menuItemMasterRepository.findByUuidAndIsDeleteFalse(newUuid)
				.stream().collect(Collectors.toMap(MenuItemMasterEntity::getNameEnglish, menuItem -> menuItem,
						(existing, replacement) -> existing));

		// Query 4: Get Units for new UUID
		Map<String, UnitMasterEntity> unitMap = unitMasterRepository.findByUuidAndIsDeleteFalse(newUuid).stream()
				.collect(Collectors.toMap(UnitMasterEntity::getNameEnglish, unit -> unit,
						(existing, replacement) -> existing));

		// Query 5: Existing records for duplicate checking
		List<MenuItemRawMaterialEntity> existingEntities = menuItemRawMaterialRepository
				.findByUuidAndIsDeleteFalse(newUuid);

		/*
		 * Duplicate key: MenuItem + RawMaterial + Unit
		 */
		Set<String> existingKeys = existingEntities.stream().filter(
				entity -> entity.getMenuItem() != null && entity.getRawMaterial() != null && entity.getUnit() != null)
				.map(entity -> entity.getMenuItem().getNameEnglish() + "|" + entity.getRawMaterial().getNameEnglish()
						+ "|" + entity.getUnit().getNameEnglish())
				.collect(Collectors.toSet());

		final LocalDateTime now = LocalDateTime.now();

		List<MenuItemRawMaterialEntity> newEntities = new ArrayList<>();

		for (MenuItemRawMaterialEntity old : oldEntities) {

			// Validate old relationships
			if (old.getMenuItem() == null || old.getRawMaterial() == null || old.getUnit() == null) {
				continue;
			}

			String menuItemName = old.getMenuItem().getNameEnglish();

			String rawMaterialName = old.getRawMaterial().getNameEnglish();

			String unitName = old.getUnit().getNameEnglish();

			// Unique key
			String key = menuItemName + "|" + rawMaterialName + "|" + unitName;

			// Skip duplicate
			if (existingKeys.contains(key)) {
				continue;
			}

			// Map new UUID entities
			MenuItemMasterEntity menuItem = menuItemMap.get(menuItemName);

			RawMaterialMasterEntity rawMaterial = rawMaterialMap.get(rawMaterialName);

			UnitMasterEntity unit = unitMap.get(unitName);

			// Skip if mapping not found
			if (menuItem == null || rawMaterial == null || unit == null) {
				continue;
			}

			// Create fresh entity
			MenuItemRawMaterialEntity clone = new MenuItemRawMaterialEntity();

			// ==============================
			// Copy normal fields
			// ==============================

			clone.setWeight(old.getWeight());
			clone.setVenue(old.getVenue());
			clone.setRate(old.getRate());
			clone.setIsActive(old.getIsActive());
			clone.setIsDelete(false);
			clone.setIsVisible(old.getIsVisible());

			// ==============================
			// New entity specific fields
			// ==============================

			clone.setIsPublished(false);
			clone.setUuid(newUuid);
			clone.setUser(userMasterEntity);
			clone.setUpdatedAt(now);

			// createdAt handled automatically by @CreationTimestamp

			// ==============================
			// Mapped relationships
			// ==============================

			clone.setMenuItem(menuItem);
			clone.setRawMaterial(rawMaterial);
			clone.setUnit(unit);

			newEntities.add(clone);

			// Prevent duplicate inside current cloning operation
			existingKeys.add(key);
		}

		// Batch insert
		if (!newEntities.isEmpty()) {
			menuItemRawMaterialRepository.saveAll(newEntities);
		}

		System.err.println("saveMenuItemRawMaterialEntity ------ out ");

	}

	@Override
	public String getOrCreateUserUuid(UserMasterEntity userMasterEntity) {
		DatabasePlanningEntity entity = databasePlanningEntityRepository.findByUser(userMasterEntity).orElse(null);
		if (Objects.nonNull(entity)) {
			return entity.getUuid();
		}
		String newUuid = UUID.randomUUID().toString();
		entity = new DatabasePlanningEntity();
		entity.setDbName(userMasterEntity.getContactNo() + "_" + userMasterEntity.getId());
		entity.setInstructions("");
		entity.setIsPublished(false);
		entity.setIsActive(true);
		entity.setIsDelete(false);
		entity.setState("NO_STATE");
		entity.setUser(userMasterEntity);
		entity.setUuid(newUuid);

		databasePlanningEntityRepository.save(entity);
		return newUuid;
	}

	@Override
	public void deleteDbPlanningEntity(Long dbPlanningId) {
		databasePlanningEntityRepository.deleteById(dbPlanningId);
	}

	@Override
	public Map<String, Object> getByDbPlanningId(String parentDbId) {
		List<DatabasePlanningEntity> list = databasePlanningEntityRepository
				.findByParentDbId(Long.parseLong(parentDbId));
		if (list.isEmpty()) {
			return ResponseUtils.createSuccessRespones("Data not found", "");
		}
		ArrayNode arrayNode = objectMapper.createArrayNode();

		list.forEach(databasePlanningEntity -> {
			ObjectNode node = objectMapper.createObjectNode();

			UserMasterEntity user = databasePlanningEntity.getUser();

			node.put("firstName", user.getFirstName());
			node.put("lastName", user.getLastName());
			node.put("email", user.getEmail());
			node.put("contactNumber", user.getContactNo());

			arrayNode.add(node);
		});

		return ResponseUtils.createSuccessRespones(arrayNode, "Data fetched successfully");
	}
}