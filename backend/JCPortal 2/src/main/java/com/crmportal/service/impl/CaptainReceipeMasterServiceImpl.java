package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.CaptainReceipeMasterEntity;
import com.crmportal.entity.CaptainReceipeRawMaterialItemEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.CaptainReceipeMasterRepository;
import com.crmportal.repository.CaptainReceipeRawMaterialItemRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.CaptainReceipeMasterRequestDto;
import com.crmportal.request.dto.CaptainReceipeRawItemsRequestDto;
import com.crmportal.response.dto.CaptainReceipeMasterResponseDto;
import com.crmportal.response.dto.CaptainReceipeRawMaterialItemResponseDto;
import com.crmportal.response.dto.UnitHierarchyDto;
import com.crmportal.service.CaptainReceipeMasterService;
import com.crmportal.service.UnitMasterService;

@Service
public class CaptainReceipeMasterServiceImpl implements CaptainReceipeMasterService {

	@Autowired
	CaptainReceipeMasterRepository captainReceipeMasterRepository;
	
	@Autowired
	RawMaterialMasterRepository rawMaterialMasterRepository;
	
	@Autowired
	UnitMasterRepository unitMasterRepository;
	
	@Autowired
	CaptainReceipeRawMaterialItemRepository captainReceipeRawMaterialItemRepository;
	
	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	UnitMasterService unitMasterService;
	
//	@Override
//	@Transactional
//	public CaptainReceipeMasterResponseDto addOrUpdateCaptainReceipeMaster(
//			@Valid CaptainReceipeMasterRequestDto request) {
//		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
//				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));
//		
//		UnitMasterEntity u = unitMasterRepository.findByIdAndIsDeleteFalse(request.getUnitId())
//				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));
//		
//		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
//		LocalDateTime now = LocalDateTime.now();
//
//		CaptainReceipeMasterEntity masterEntity;
//
//		if (request.getId() != null && request.getId() != -1) {
//			masterEntity = captainReceipeMasterRepository.findById(request.getId())
//					.orElseThrow(() -> new RuntimeException("Captain receipe not found."));
//
//			masterEntity.setUpdatedAt(now);
//		} else {
//			masterEntity = new CaptainReceipeMasterEntity();
//			masterEntity.setIsActive(true);
//		}
//
//		masterEntity.setUserId(user.getId());
//		masterEntity.setName(request.getName());
//		masterEntity.setWeight(request.getWeight());
//		masterEntity.setRate(request.getRate());
//		masterEntity.setUnitId(request.getUnitId());
//		
//		masterEntity = captainReceipeMasterRepository.save(masterEntity);
//
//		Set<Long> rawItemIds = request.getRawItems().stream().map(CaptainReceipeRawItemsRequestDto::getRawItemId)
//				.collect(Collectors.toSet());
//
//		Set<Long> unitIds = request.getRawItems().stream().map(CaptainReceipeRawItemsRequestDto::getUnitId)
//				.collect(Collectors.toSet());
//
//		Map<Long, RawMaterialMasterEntity> rawMaterialMap = rawMaterialMasterRepository.findAllById(rawItemIds).stream()
//				.collect(Collectors.toMap(RawMaterialMasterEntity::getId, Function.identity()));
//
//		Map<Long, UnitMasterEntity> unitMap = unitMasterRepository.findAllById(unitIds).stream()
//				.collect(Collectors.toMap(UnitMasterEntity::getId, Function.identity()));
//
//		Set<Long> rawItemEntityIds = request.getRawItems().stream().map(CaptainReceipeRawItemsRequestDto::getId)
//				.filter(Objects::nonNull).filter(id -> id > 0).collect(Collectors.toSet());
//
//		Map<Long, CaptainReceipeRawMaterialItemEntity> existingRawItemMap = rawItemEntityIds.isEmpty()
//				? Collections.emptyMap()
//				: captainReceipeRawMaterialItemRepository.findAllById(rawItemEntityIds).stream()
//						.collect(Collectors.toMap(CaptainReceipeRawMaterialItemEntity::getId, Function.identity()));
//
//		List<CaptainReceipeRawMaterialItemEntity> rawEntities = new ArrayList<>();
//
//		for (CaptainReceipeRawItemsRequestDto rawItem : request.getRawItems()) {
//			RawMaterialMasterEntity rawMaterial = rawMaterialMap.get(rawItem.getRawItemId());
//
//			if (rawMaterial == null) {
//				throw new RuntimeException("Raw material not found : " + rawItem.getRawItemId());
//			}
//
//			UnitMasterEntity unit = unitMap.get(rawItem.getUnitId());
//
//			if (unit == null) {
//				throw new RuntimeException("Unit not found : " + rawItem.getUnitId());
//			}
//
//			CaptainReceipeRawMaterialItemEntity rawEntity;
//
//			if (rawItem.getId() != null && rawItem.getId() != -1) {
//
//				rawEntity = existingRawItemMap.get(rawItem.getId());
//
//				if (rawEntity == null) {
//					throw new RuntimeException("Raw material item not found : " + rawItem.getId());
//				}
//
//				if (!rawEntity.getCaptainReceipeId().equals(masterEntity.getId())) {
//
//					throw new RuntimeException("Raw material item does not belong to this receipe.");
//				}
//
//				rawEntity.setUpdatedAt(now);
//
//			} else {
//				rawEntity = new CaptainReceipeRawMaterialItemEntity();
//				rawEntity.setCaptainReceipeId(masterEntity.getId());
//				rawEntity.setIsDelete(false);
//			}
//
//			rawEntity.setRawItemId(rawMaterial.getId());
//			rawEntity.setQty(new BigDecimal(rawItem.getQty().toString()));
//			rawEntity.setUnitId(unit.getId());
//			rawEntity.setUnitName(unit.getNameEnglish());
//			rawEntity.setRate(rawItem.getRate());
//
//			rawEntities.add(rawEntity);
//		}
//
//		List<CaptainReceipeRawMaterialItemEntity> savedRawEntities = captainReceipeRawMaterialItemRepository.saveAll(rawEntities);
//
//		List<CaptainReceipeRawMaterialItemResponseDto> rawMaterialResponse = savedRawEntities.stream().map(item -> {
//
//			RawMaterialMasterEntity rawMaterial = rawMaterialMap.get(item.getRawItemId());
//
//			return new CaptainReceipeRawMaterialItemResponseDto(item.getId(), item.getRawItemId(),
//					rawMaterial != null ? rawMaterial.getNameEnglish() : null, item.getCaptainReceipeId(),
//					item.getQty(), item.getUnitId(), item.getUnitName(), item.getIsDelete(),
//					formatDate(item.getCreatedAt(), dateTimeFormat), formatDate(item.getUpdatedAt(), dateTimeFormat), item.getRate());
//		}).collect(Collectors.toList());
//
//		UnitHierarchyDto unitHierarchy = unitMasterService.getParentUnitsWithChildren(request.getUnitId());
//		
//		return new CaptainReceipeMasterResponseDto(masterEntity.getId(), masterEntity.getName(),
//				masterEntity.getUserId(), masterEntity.getWeight(), masterEntity.getUnitId(), unitHierarchy, masterEntity.getRate(),
//				masterEntity.getIsActive(), masterEntity.getIsDelete(), 
//				formatDate(masterEntity.getCreatedAt(), dateTimeFormat), formatDate(masterEntity.getUpdatedAt(), dateTimeFormat), 
//				rawMaterialResponse);
//	}
	
	@Override
	@Transactional
	public CaptainReceipeMasterResponseDto addOrUpdateCaptainReceipeMaster(
			@Valid CaptainReceipeMasterRequestDto request) {

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));

		unitMasterRepository.findByIdAndIsDeleteFalse(request.getUnitId())
				.orElseThrow(() -> new RuntimeException("Unit not found with id : " + request.getUnitId()));

		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();

		CaptainReceipeMasterEntity masterEntity;

		if (request.getId() != null && request.getId() != -1) {
			masterEntity = captainReceipeMasterRepository.findById(request.getId())
					.orElseThrow(() -> new RuntimeException("Captain receipe not found."));

		} else {
			masterEntity = new CaptainReceipeMasterEntity();
			masterEntity.setIsActive(true);
			masterEntity.setIsDelete(false);
		}

		masterEntity.setUserId(user.getId());
		masterEntity.setName(request.getName());
		masterEntity.setWeight(request.getWeight());
		masterEntity.setRate(request.getRate());
		masterEntity.setUnitId(request.getUnitId());

		masterEntity = captainReceipeMasterRepository.save(masterEntity);

		if (request.getId() != null && request.getId() != -1) {

			List<CaptainReceipeRawMaterialItemEntity> existingItems = captainReceipeRawMaterialItemRepository
					.findByCaptainReceipeIdAndIsDeleteFalse(masterEntity.getId());

			Set<Long> requestItemIds = request.getRawItems().stream().map(CaptainReceipeRawItemsRequestDto::getId)
					.filter(Objects::nonNull).filter(id -> id > 0).collect(Collectors.toSet());

			List<CaptainReceipeRawMaterialItemEntity> itemsToDelete = existingItems.stream()
					.filter(item -> !requestItemIds.contains(item.getId())).collect(Collectors.toList());

			if (!itemsToDelete.isEmpty()) {
				itemsToDelete.forEach(item -> {
					item.setIsDelete(true);
					item.setUpdatedAt(now);
				});

				captainReceipeRawMaterialItemRepository.saveAll(itemsToDelete);
			}
		}

		Set<Long> rawItemIds = request.getRawItems().stream().map(CaptainReceipeRawItemsRequestDto::getRawItemId)
				.collect(Collectors.toSet());

		Set<Long> unitIds = request.getRawItems().stream().map(CaptainReceipeRawItemsRequestDto::getUnitId)
				.collect(Collectors.toSet());

		Map<Long, RawMaterialMasterEntity> rawMaterialMap = rawMaterialMasterRepository.findAllById(rawItemIds).stream()
				.collect(Collectors.toMap(RawMaterialMasterEntity::getId, Function.identity()));

		Map<Long, UnitMasterEntity> unitMap = unitMasterRepository.findAllById(unitIds).stream()
				.collect(Collectors.toMap(UnitMasterEntity::getId, Function.identity()));

		Set<Long> rawItemEntityIds = request.getRawItems().stream().map(CaptainReceipeRawItemsRequestDto::getId)
				.filter(Objects::nonNull).filter(id -> id > 0).collect(Collectors.toSet());

		Map<Long, CaptainReceipeRawMaterialItemEntity> existingRawItemMap = rawItemEntityIds.isEmpty()
				? Collections.emptyMap()
				: captainReceipeRawMaterialItemRepository.findAllById(rawItemEntityIds).stream()
						.collect(Collectors.toMap(CaptainReceipeRawMaterialItemEntity::getId, Function.identity()));

		List<CaptainReceipeRawMaterialItemEntity> rawEntities = new ArrayList<>();

		for (CaptainReceipeRawItemsRequestDto rawItem : request.getRawItems()) {

			RawMaterialMasterEntity rawMaterial = rawMaterialMap.get(rawItem.getRawItemId());

			if (rawMaterial == null) {
				throw new RuntimeException("Raw material not found : " + rawItem.getRawItemId());
			}

			UnitMasterEntity unit = unitMap.get(rawItem.getUnitId());

			if (unit == null) {
				throw new RuntimeException("Unit not found : " + rawItem.getUnitId());
			}

			CaptainReceipeRawMaterialItemEntity rawEntity;

			if (rawItem.getId() != null && rawItem.getId() != -1) {

				rawEntity = existingRawItemMap.get(rawItem.getId());

				if (rawEntity == null) {
					throw new RuntimeException("Raw material item not found : " + rawItem.getId());
				}

				if (!rawEntity.getCaptainReceipeId().equals(masterEntity.getId())) {
					throw new RuntimeException("Raw material item does not belong to this receipe.");
				}

				rawEntity.setUpdatedAt(now);

			} else {
				rawEntity = new CaptainReceipeRawMaterialItemEntity();
				rawEntity.setCaptainReceipeId(masterEntity.getId());
				rawEntity.setIsDelete(false);
			}

			rawEntity.setRawItemId(rawMaterial.getId());
			rawEntity.setQty(rawItem.getQty());
			rawEntity.setUnitId(unit.getId());
			rawEntity.setUnitName(unit.getNameEnglish());
			rawEntity.setRate(rawItem.getRate());

			rawEntities.add(rawEntity);
		}

		List<CaptainReceipeRawMaterialItemEntity> savedRawEntities = captainReceipeRawMaterialItemRepository
				.saveAll(rawEntities);

		List<CaptainReceipeRawMaterialItemResponseDto> rawMaterialResponse = savedRawEntities.stream().map(item -> {

			RawMaterialMasterEntity rawMaterial = rawMaterialMap.get(item.getRawItemId());

			return new CaptainReceipeRawMaterialItemResponseDto(item.getId(), item.getRawItemId(),
					rawMaterial != null ? rawMaterial.getNameEnglish() : null, item.getCaptainReceipeId(),
					item.getQty(), item.getUnitId(), item.getUnitName(), item.getIsDelete(),
					formatDate(item.getCreatedAt(), dateTimeFormat), formatDate(item.getUpdatedAt(), dateTimeFormat),
					item.getRate());

		}).collect(Collectors.toList());

		UnitHierarchyDto unitHierarchy = unitMasterService.getParentUnitsWithChildren(request.getUnitId());

		return new CaptainReceipeMasterResponseDto(masterEntity.getId(), masterEntity.getName(),
				masterEntity.getUserId(), masterEntity.getWeight(), masterEntity.getUnitId(), unitHierarchy.getNameEnglish(), 
				unitHierarchy,
				masterEntity.getRate(), masterEntity.getIsActive(), masterEntity.getIsDelete(),
				formatDate(masterEntity.getCreatedAt(), dateTimeFormat),
				formatDate(masterEntity.getUpdatedAt(), dateTimeFormat), rawMaterialResponse);
	}

	@Override
	public List<CaptainReceipeMasterResponseDto> getAllCaptainReceipeByUserId(Long userId, Boolean status) {
		userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

		List<CaptainReceipeMasterEntity> masterList = captainReceipeMasterRepository
				.findAllByUserId(userId, status);

		if (masterList.isEmpty()) {
			return Collections.emptyList();
		}

		Set<Long> captainReceipeIds = masterList.stream().map(CaptainReceipeMasterEntity::getId)
				.collect(Collectors.toSet());

		List<CaptainReceipeRawMaterialItemEntity> rawMaterialItems = captainReceipeRawMaterialItemRepository
				.findByCaptainReceipeIdInAndIsDeleteFalse(captainReceipeIds);

		Map<Long, List<CaptainReceipeRawMaterialItemEntity>> rawMaterialMap = rawMaterialItems.stream()
				.collect(Collectors.groupingBy(CaptainReceipeRawMaterialItemEntity::getCaptainReceipeId));

		Set<Long> rawMaterialIds = rawMaterialItems.stream().map(CaptainReceipeRawMaterialItemEntity::getRawItemId)
				.collect(Collectors.toSet());

		Map<Long, RawMaterialMasterEntity> rawMaterialMasterMap = rawMaterialIds.isEmpty() ? Collections.emptyMap()
				: rawMaterialMasterRepository.findAllById(rawMaterialIds).stream()
						.collect(Collectors.toMap(RawMaterialMasterEntity::getId, Function.identity()));

		return masterList.stream().map(master -> {

			List<CaptainReceipeRawMaterialItemResponseDto> rawMaterials = rawMaterialMap
					.getOrDefault(master.getId(), Collections.emptyList()).stream().map(item -> {

						String rawMaterialName = Optional.ofNullable(rawMaterialMasterMap.get(item.getRawItemId()))
								.map(RawMaterialMasterEntity::getNameEnglish).orElse(null);

						return new CaptainReceipeRawMaterialItemResponseDto(item.getId(), item.getRawItemId(),
								rawMaterialName, item.getCaptainReceipeId(), item.getQty(), item.getUnitId(),
								item.getUnitName(), item.getIsDelete(), formatDate(item.getCreatedAt(), dateTimeFormat),
								formatDate(item.getUpdatedAt(), dateTimeFormat), item.getRate());
					}).collect(Collectors.toList());

			UnitHierarchyDto unitHierarchy = unitMasterService.getParentUnitsWithChildren(master.getUnitId());
			
			return new CaptainReceipeMasterResponseDto(master.getId(), master.getName(), master.getUserId(),
					master.getWeight(), master.getUnitId(), unitHierarchy.getNameEnglish(),
					unitHierarchy, master.getRate(), master.getIsActive(), master.getIsDelete(), 
					formatDate(master.getCreatedAt(), dateTimeFormat), formatDate(master.getUpdatedAt(), dateTimeFormat), 
					rawMaterials);
		}).collect(Collectors.toList());
	}

	@Override
	public CaptainReceipeMasterResponseDto getCaptainReceipeById(Long id, Boolean isSync) {

		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

		CaptainReceipeMasterEntity master = captainReceipeMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Captain receipe not found with id : " + id));

		List<CaptainReceipeRawMaterialItemEntity> rawMaterialItems = captainReceipeRawMaterialItemRepository
				.findByCaptainReceipeIdAndIsDeleteFalse(master.getId());

		Set<Long> rawMaterialIds = rawMaterialItems.stream().map(CaptainReceipeRawMaterialItemEntity::getRawItemId)
				.collect(Collectors.toSet());

		Map<Long, RawMaterialMasterEntity> rawMaterialMasterMap = rawMaterialIds.isEmpty() ? Collections.emptyMap()
				: rawMaterialMasterRepository.findAllById(rawMaterialIds).stream()
						.collect(Collectors.toMap(RawMaterialMasterEntity::getId, Function.identity()));

		Map<Long, UnitMasterEntity> unitMasterMap = Collections.emptyMap();

		if (Boolean.TRUE.equals(isSync)) {

			Set<Long> unitIds = rawMaterialItems.stream().map(CaptainReceipeRawMaterialItemEntity::getUnitId)
					.collect(Collectors.toSet());

			unitMasterMap = unitIds.isEmpty() ? Collections.emptyMap()
					: unitMasterRepository.findAllById(unitIds).stream()
							.collect(Collectors.toMap(UnitMasterEntity::getId, Function.identity()));
		}

		final Map<Long, UnitMasterEntity> finalUnitMasterMap = unitMasterMap;

		List<CaptainReceipeRawMaterialItemResponseDto> rawMaterials = rawMaterialItems.stream().map(item -> {

			RawMaterialMasterEntity rawMaster = rawMaterialMasterMap.get(item.getRawItemId());

			BigDecimal rate = item.getRate();

			if (Boolean.TRUE.equals(isSync) && rawMaster != null) {

				UnitMasterEntity unitMaster = finalUnitMasterMap.get(item.getUnitId());

				BigDecimal qty = Optional.ofNullable(item.getQty()).orElse(BigDecimal.ZERO);
				BigDecimal supplierRate = Optional.ofNullable(rawMaster.getSupplierRate()).orElse(BigDecimal.ZERO);

				if (unitMaster != null) {

					if (Objects.equals(rawMaster.getUnit().getId(), item.getUnitId())) {

						rate = supplierRate.multiply(qty);

					} else if (Boolean.TRUE.equals(unitMaster.getIsParentUnit())) {

						rate = supplierRate.multiply(qty).multiply(BigDecimal.valueOf(1000));

					} else {

						rate = supplierRate.multiply(qty).divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP);
					}
				}
			}

			String rawMaterialName = rawMaster != null ? rawMaster.getNameEnglish() : null;

			return new CaptainReceipeRawMaterialItemResponseDto(item.getId(), item.getRawItemId(), rawMaterialName,
					item.getCaptainReceipeId(), item.getQty(), item.getUnitId(), item.getUnitName(), item.getIsDelete(),
					formatDate(item.getCreatedAt(), dateTimeFormat), formatDate(item.getUpdatedAt(), dateTimeFormat),
					rate);
		}).collect(Collectors.toList());

		UnitHierarchyDto unitHierarchy = unitMasterService.getParentUnitsWithChildren(master.getUnitId());

		return new CaptainReceipeMasterResponseDto(master.getId(), master.getName(), master.getUserId(),
				master.getWeight(), master.getUnitId(), unitHierarchy.getNameEnglish(),
				unitHierarchy, master.getRate(), master.getIsActive(),
				master.getIsDelete(), formatDate(master.getCreatedAt(), dateTimeFormat),
				formatDate(master.getUpdatedAt(), dateTimeFormat), rawMaterials);
	}
	
	@Override
	public Boolean deleteCaptainReceipeById(Long id) {
		LocalDateTime now = LocalDateTime.now();

		CaptainReceipeMasterEntity master = captainReceipeMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Captain receipe not found with id : " + id));

		master.setIsDelete(true);
		master.setUpdatedAt(now);

		captainReceipeMasterRepository.save(master);

		return true;
	}
	
	@Override
	public Boolean updateCaptainReceipeStatusById(Long id, Boolean status) {
		if (status == null) {
			throw new RuntimeException("Status is required.");
		}

		CaptainReceipeMasterEntity master = captainReceipeMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Captain receipe not found with id : " + id));

		master.setIsActive(status);
		master.setUpdatedAt(LocalDateTime.now());

		captainReceipeMasterRepository.save(master);

		return true;
	}
	
	private String formatDate(LocalDateTime dateTime, DateTimeFormatter dateTimeFormatter) {
	    return dateTime != null ? dateTime.format(dateTimeFormatter) : null;
	}
	
	public Boolean syncAllCaptainReceipeRawMaterial(Long userId) {
		userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

		List<Long> captReceipeIds = captainReceipeMasterRepository.findAllByUserIdAndIsDeleteFalse(userId).stream()
				.map(CaptainReceipeMasterEntity::getId).collect(Collectors.toList());

		if (captReceipeIds.isEmpty()) {
			return true;
		}

		List<CaptainReceipeRawMaterialItemEntity> rawItems = captainReceipeRawMaterialItemRepository
				.findAllByCaptainReceipeIdInAndIsDeleteFalse(captReceipeIds);

		Set<Long> rawMaterialIds = rawItems.stream().map(CaptainReceipeRawMaterialItemEntity::getRawItemId)
				.collect(Collectors.toSet());

		Map<Long, RawMaterialMasterEntity> rawMaterialMasterMap = rawMaterialMasterRepository
				.findAllByIdInAndIsDeleteFalse(new ArrayList<>(rawMaterialIds)).stream()
				.collect(Collectors.toMap(RawMaterialMasterEntity::getId, Function.identity()));

		Set<Long> unitIds = rawItems.stream().map(CaptainReceipeRawMaterialItemEntity::getUnitId)
				.collect(Collectors.toSet());

		Map<Long, UnitMasterEntity> unitMasterMap = unitMasterRepository.findAllById(unitIds).stream()
				.collect(Collectors.toMap(UnitMasterEntity::getId, Function.identity()));

		rawItems.forEach(item -> {
			RawMaterialMasterEntity rawMaster = rawMaterialMasterMap.get(item.getRawItemId());

			if (rawMaster == null) {
				return;
			}

			UnitMasterEntity unitMaster = unitMasterMap.get(item.getUnitId());

			if (unitMaster == null) {
				return;
			}

			BigDecimal qty = Optional.ofNullable(item.getQty()).orElse(BigDecimal.ZERO);

			BigDecimal supplierRate = Optional.ofNullable(rawMaster.getSupplierRate()).orElse(BigDecimal.ZERO);

			BigDecimal rate;

			if (Objects.equals(rawMaster.getUnit().getId(), item.getUnitId())) {
				rate = supplierRate.multiply(qty);
			} else if (Boolean.TRUE.equals(unitMaster.getIsParentUnit())) {
				rate = supplierRate.multiply(qty).multiply(BigDecimal.valueOf(1000));
			} else {
				rate = supplierRate.multiply(qty).divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP);
			}

			item.setRate(rate);
		});

		captainReceipeRawMaterialItemRepository.saveAll(rawItems);

		return true;
	}
}
