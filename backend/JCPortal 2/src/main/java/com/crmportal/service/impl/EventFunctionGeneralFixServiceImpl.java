package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventFunctionGeneralFixEntity;
import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventGeneralFixEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.RawMaterialCategoryMasterEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.repository.EventFunctionGeneralFixRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventGeneralFixRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.RawMaterialCategoryMasterRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.request.dto.EventFunctionGeneralFixRawRequestDto;
import com.crmportal.request.dto.EventGeneralFixRawRequestDto;
import com.crmportal.request.dto.GeneralFixRequestDto;
import com.crmportal.response.dto.EventFunctionGeneralFixRawResponseDto;
import com.crmportal.response.dto.EventGeneralFixRawResponseDto;
import com.crmportal.response.dto.GeneralFixRawResponseDto;
import com.crmportal.response.dto.UnitConversionResult;
import com.crmportal.service.EventFunctionGeneralFixService;
import com.crmportal.service.UnitMasterService;
import com.crmportal.utility.RoundOffUtility;

@Service
public class EventFunctionGeneralFixServiceImpl implements EventFunctionGeneralFixService {

	@Autowired
	EventFunctionGeneralFixRepository eventFunctionGeneralFixRepository;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;

	@Autowired
	UnitMasterRepository unitMasterRepository;

	@Autowired
	RawMaterialCategoryMasterRepository rawMaterialCategoryMasterRepository;

	@Autowired
	RawMaterialMasterRepository rawMaterialMasterRepository;

	@Autowired
	EventGeneralFixRepository eventGeneralFixRepository;

	@Autowired
	private UnitMasterService unitMasterService;

	@Autowired
	RoundOffUtility roundOffUtility;

	@Autowired
	UnitConversionServiceImpl unitConversionService;

	private static final Long RAW_MATERIAL_CATEGORY_TYPE_ID = 1L;

	@Override
	@Transactional
	public Boolean addUpdate(GeneralFixRequestDto requestDto) {

		if (requestDto.getGeneralFixRaws() == null || requestDto.getGeneralFixRaws().isEmpty()) {
			return false;
		}

		EventMasterEntity eventMaster = eventMasterRepository.findByIdAndIsDeleteFalse(requestDto.getEventId())
				.orElseThrow(() -> new RuntimeException("Event Master Not Found"));

		List<EventGeneralFixRawRequestDto> generalFixRequests = requestDto.getGeneralFixRaws();

		List<Long> rawIds = generalFixRequests.stream().map(EventGeneralFixRawRequestDto::getRawId)
				.filter(Objects::nonNull).distinct().collect(Collectors.toList());

		List<Long> rawCatIds = generalFixRequests.stream().map(EventGeneralFixRawRequestDto::getRawCatId)
				.filter(Objects::nonNull).distinct().collect(Collectors.toList());

		List<Long> unitIds = generalFixRequests.stream().map(EventGeneralFixRawRequestDto::getUnitId)
				.filter(Objects::nonNull).distinct().collect(Collectors.toList());

		List<Long> eventFunctionIds = generalFixRequests.stream()
				.filter(x -> x.getEventFunctionGeneralFixRaws() != null)
				.flatMap(x -> x.getEventFunctionGeneralFixRaws().stream())
				.map(EventFunctionGeneralFixRawRequestDto::getEventFunctionId).filter(Objects::nonNull).distinct()
				.collect(Collectors.toList());

		Map<Long, RawMaterialMasterEntity> rawMaterialMap = rawMaterialMasterRepository.findAllById(rawIds).stream()
				.collect(Collectors.toMap(RawMaterialMasterEntity::getId, Function.identity()));

		Map<Long, RawMaterialCategoryMasterEntity> rawCategoryMap = rawMaterialCategoryMasterRepository
				.findAllById(rawCatIds).stream()
				.collect(Collectors.toMap(RawMaterialCategoryMasterEntity::getId, Function.identity()));

		Map<Long, UnitMasterEntity> unitMap = unitMasterRepository.findAllById(unitIds).stream()
				.collect(Collectors.toMap(UnitMasterEntity::getId, Function.identity()));

		Map<Long, EventFunctionMasterEntity> eventFunctionMap = eventFunctionMasterRepository
				.findAllById(eventFunctionIds).stream()
				.collect(Collectors.toMap(EventFunctionMasterEntity::getId, Function.identity()));

		List<Long> existingGeneralFixIds = generalFixRequests.stream().map(EventGeneralFixRawRequestDto::getId)
				.filter(id -> id != null && id > 0).distinct().collect(Collectors.toList());

		Map<Long, EventGeneralFixEntity> existingGeneralFixMap = eventGeneralFixRepository
				.findAllById(existingGeneralFixIds).stream()
				.collect(Collectors.toMap(EventGeneralFixEntity::getId, Function.identity()));

		List<Long> existingFunctionFixIds = generalFixRequests.stream()
				.filter(x -> x.getEventFunctionGeneralFixRaws() != null)
				.flatMap(x -> x.getEventFunctionGeneralFixRaws().stream())
				.map(EventFunctionGeneralFixRawRequestDto::getId).filter(id -> id != null && id > 0).distinct()
				.collect(Collectors.toList());

		Map<Long, EventFunctionGeneralFixEntity> existingFunctionFixMap = eventFunctionGeneralFixRepository
				.findAllById(existingFunctionFixIds).stream()
				.collect(Collectors.toMap(EventFunctionGeneralFixEntity::getId, Function.identity()));

		List<EventGeneralFixEntity> generalFixEntities = new ArrayList<>();

		for (EventGeneralFixRawRequestDto generalFixRequest : generalFixRequests) {

			RawMaterialMasterEntity rawMaterial = rawMaterialMap.get(generalFixRequest.getRawId());

			if (rawMaterial == null) {
				throw new RuntimeException("Raw Material Not Found: " + generalFixRequest.getRawId());
			}

			RawMaterialCategoryMasterEntity rawCategory = rawCategoryMap.get(generalFixRequest.getRawCatId());

			if (rawCategory == null) {
				throw new RuntimeException("Raw Material Category Not Found: " + generalFixRequest.getRawCatId());
			}

			UnitMasterEntity unit = unitMap.get(generalFixRequest.getUnitId());

			if (unit == null) {
				throw new RuntimeException("Unit Master Not Found: " + generalFixRequest.getUnitId());
			}

			EventGeneralFixEntity generalFixEntity;

			if (generalFixRequest.getId() != null && generalFixRequest.getId() > 0) {

				generalFixEntity = existingGeneralFixMap.get(generalFixRequest.getId());

				if (generalFixEntity == null) {
					throw new RuntimeException("Event General Fix Not Found: " + generalFixRequest.getId());
				}

			} else {
				generalFixEntity = new EventGeneralFixEntity();
			}

			generalFixEntity.setEventId(eventMaster.getId());
			generalFixEntity.setRawCatId(rawCategory.getId());
			generalFixEntity.setRawId(rawMaterial.getId());
			generalFixEntity.setUnitId(unit.getId());
			generalFixEntity.setWeight(generalFixRequest.getWeight());
			generalFixEntity.setPrice(generalFixRequest.getPrice());
			generalFixEntity.setSourceWeightPer100Pax(safe(rawMaterial.getWeightPer100Pax()));
			generalFixEntity.setSourceSupplierRate(safe(rawMaterial.getSupplierRate()));
			generalFixEntity.setSourceUnitId(rawMaterial.getUnit() != null ? rawMaterial.getUnit().getId() : null);
			generalFixEntities.add(generalFixEntity);
		}

		eventGeneralFixRepository.saveAll(generalFixEntities);

		List<EventFunctionGeneralFixEntity> functionFixEntities = new ArrayList<>();

		for (int i = 0; i < generalFixRequests.size(); i++) {

			EventGeneralFixRawRequestDto generalFixRequest = generalFixRequests.get(i);

			EventGeneralFixEntity generalFixEntity = generalFixEntities.get(i);

			if (generalFixRequest.getEventFunctionGeneralFixRaws() == null
					|| generalFixRequest.getEventFunctionGeneralFixRaws().isEmpty()) {
				continue;
			}

			for (EventFunctionGeneralFixRawRequestDto functionRequest : generalFixRequest
					.getEventFunctionGeneralFixRaws()) {

				EventFunctionMasterEntity eventFunction = eventFunctionMap.get(functionRequest.getEventFunctionId());

				RawMaterialMasterEntity rawMaterial = rawMaterialMap.get(generalFixEntity.getRawId());

				if (rawMaterial == null) {
					throw new RuntimeException("Raw Material Not Found: " + generalFixEntity.getRawId());
				}

				if (eventFunction == null) {
					throw new RuntimeException("Event Function Not Found: " + functionRequest.getEventFunctionId());
				}

				EventFunctionGeneralFixEntity functionFixEntity;

				if (functionRequest.getId() != null && functionRequest.getId() > 0) {

					functionFixEntity = existingFunctionFixMap.get(functionRequest.getId());

					if (functionFixEntity == null) {
						throw new RuntimeException("Event Function General Fix Not Found: " + functionRequest.getId());
					}

				} else {
					functionFixEntity = new EventFunctionGeneralFixEntity();
				}

				functionFixEntity.setEventGeneralfixId(generalFixEntity.getId());

				functionFixEntity.setEventFunctionId(eventFunction.getId());

				functionFixEntity.setRawCatId(generalFixEntity.getRawCatId());

				functionFixEntity.setRawId(generalFixEntity.getRawId());

				functionFixEntity.setUnitId(generalFixEntity.getUnitId());

				functionFixEntity.setWeight(functionRequest.getWeight());

				functionFixEntity.setPrice(functionRequest.getPrice());
				functionFixEntity.setSourceWeightPer100Pax(safe(rawMaterial.getWeightPer100Pax()));
				functionFixEntity.setSourceSupplierRate(safe(rawMaterial.getSupplierRate()));
				functionFixEntity.setSourceUnitId(rawMaterial.getUnit() != null ? rawMaterial.getUnit().getId() : null);
				functionFixEntities.add(functionFixEntity);
			}
		}

		if (!functionFixEntities.isEmpty()) {
			eventFunctionGeneralFixRepository.saveAll(functionFixEntities);
		}

		return true;
	}

	@Override
	@Transactional
	public GeneralFixRawResponseDto getAllGeneralFixRaw(Long eventId, List<Long> eventFunctionIds,
			List<Long> rawCatIds) {

		EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
				.orElseThrow(() -> new RuntimeException("Event Not Found"));

		List<EventFunctionMasterEntity> eventFunctions;

		if (eventFunctionIds == null || eventFunctionIds.isEmpty()) {

			eventFunctions = eventFunctionMasterRepository.findAllByEventAndIsDeleteFalseOrderBySortorderAsc(event);

		} else {

			eventFunctions = eventFunctionMasterRepository.findByEventAndIdInAndIsDeleteFalseOrderBySortorderAsc(event,
					eventFunctionIds);
		}
		
		List<RawMaterialMasterEntity> masterGeneralFixRaws;

		if (rawCatIds != null && !rawCatIds.isEmpty()) {

			masterGeneralFixRaws = rawMaterialMasterRepository
					.findGeneralFixRawMaterialsByCategory(RAW_MATERIAL_CATEGORY_TYPE_ID, rawCatIds);

		} else {

			masterGeneralFixRaws = rawMaterialMasterRepository
					.findGeneralFixRawMaterials(RAW_MATERIAL_CATEGORY_TYPE_ID);
		}

		Set<Long> masterRawIds = masterGeneralFixRaws.stream().map(RawMaterialMasterEntity::getId)
				.filter(Objects::nonNull).collect(Collectors.toSet());

		List<EventGeneralFixEntity> savedGeneralFixes;

		if (rawCatIds != null && !rawCatIds.isEmpty()) {

			savedGeneralFixes = eventGeneralFixRepository.findByEventIdAndRawCatIdIn(eventId, rawCatIds);

		} else {

			savedGeneralFixes = eventGeneralFixRepository.findByEventId(eventId);
		}

		removeObsoleteGeneralFixes(savedGeneralFixes, masterRawIds);

		List<EventGeneralFixEntity> validSavedGeneralFixes = savedGeneralFixes.stream()
				.filter(saved -> saved.getRawId() != null && masterRawIds.contains(saved.getRawId()))
				.collect(Collectors.toList());

		Map<Long, EventGeneralFixEntity> savedByRawId = validSavedGeneralFixes.stream()
				.collect(Collectors.toMap(EventGeneralFixEntity::getRawId, Function.identity(), (a, b) -> a));

		List<Long> generalFixIds = validSavedGeneralFixes.stream().map(EventGeneralFixEntity::getId)
				.filter(Objects::nonNull).collect(Collectors.toList());

		Map<Long, List<EventFunctionGeneralFixEntity>> functionFixByGeneralFixId;

		if (generalFixIds.isEmpty()) {

			functionFixByGeneralFixId = Collections.emptyMap();

		} else {

			functionFixByGeneralFixId = eventFunctionGeneralFixRepository.findByEventGeneralfixIdIn(generalFixIds)
					.stream().collect(Collectors.groupingBy(EventFunctionGeneralFixEntity::getEventGeneralfixId));
		}

		List<EventGeneralFixRawResponseDto> finalResponses = new ArrayList<>();

		for (RawMaterialMasterEntity rawMaterial : masterGeneralFixRaws) {

			EventGeneralFixEntity savedGeneralFix = savedByRawId.get(rawMaterial.getId());

			List<EventFunctionGeneralFixEntity> existingFunctionFixes = savedGeneralFix == null
					? Collections.emptyList()
					: functionFixByGeneralFixId.getOrDefault(savedGeneralFix.getId(), Collections.emptyList());

			EventGeneralFixRawResponseDto rawResponse = buildRawResponse(
					savedGeneralFix != null ? savedGeneralFix.getId() : null, rawMaterial, eventFunctions,
					existingFunctionFixes);

			finalResponses.add(rawResponse);
		}

		// =====================================================
		// STEP 10: FINAL RESPONSE
		// =====================================================

		GeneralFixRawResponseDto response = new GeneralFixRawResponseDto();

		response.setEventId(eventId);
		response.setEventGeneralFixRaws(finalResponses);

		return response;
	}

	private GeneralFixRawResponseDto getFromNewTables(Long eventId, List<EventFunctionMasterEntity> eventFunctions,
			List<Long> rawCatIds) {

		GeneralFixRawResponseDto response = new GeneralFixRawResponseDto();
		response.setEventId(eventId);

		List<EventGeneralFixEntity> generalFixEntities;

		if (rawCatIds != null && !rawCatIds.isEmpty()) {
			generalFixEntities = eventGeneralFixRepository.findByEventIdAndRawCatIdIn(eventId, rawCatIds);
		} else {
			generalFixEntities = eventGeneralFixRepository.findByEventId(eventId);
		}

		if (generalFixEntities == null || generalFixEntities.isEmpty()) {
			response.setEventGeneralFixRaws(Collections.emptyList());
			return response;
		}

		List<Long> rawIds = generalFixEntities.stream().map(EventGeneralFixEntity::getRawId).filter(Objects::nonNull)
				.distinct().collect(Collectors.toList());

		Map<Long, RawMaterialMasterEntity> rawMaterialMap = rawIds.isEmpty() ? Collections.emptyMap()
				: rawMaterialMasterRepository.findAllById(rawIds).stream()
						.collect(Collectors.toMap(RawMaterialMasterEntity::getId, Function.identity()));

		// NEW: pull all existing function-fix rows for these general-fix ids in one
		// shot
		List<Long> generalFixIds = generalFixEntities.stream().map(EventGeneralFixEntity::getId)
				.collect(Collectors.toList());

		Map<Long, List<EventFunctionGeneralFixEntity>> functionFixByGeneralFixId = eventFunctionGeneralFixRepository
				.findByEventGeneralfixIdIn(generalFixIds).stream()
				.collect(Collectors.groupingBy(EventFunctionGeneralFixEntity::getEventGeneralfixId));

		List<EventGeneralFixRawResponseDto> rawResponses = new ArrayList<>();

		for (EventGeneralFixEntity generalFix : generalFixEntities) {

			RawMaterialMasterEntity rawMaterial = rawMaterialMap.get(generalFix.getRawId());
			if (rawMaterial == null) {
				continue;
			}

			List<EventFunctionGeneralFixEntity> existingFunctionFixes = functionFixByGeneralFixId
					.getOrDefault(generalFix.getId(), Collections.emptyList());
			System.out.println("existingFunctionFixes:- " + existingFunctionFixes.size());
			EventGeneralFixRawResponseDto rawResponse = buildRawResponse(generalFix.getId(), rawMaterial,
					eventFunctions, existingFunctionFixes);

			rawResponses.add(rawResponse);
		}

		response.setEventGeneralFixRaws(rawResponses);
		return response;
	}

	private void removeObsoleteGeneralFixes(List<EventGeneralFixEntity> savedGeneralFixes, Set<Long> masterRawIds) {

		List<EventGeneralFixEntity> obsoleteRecords = savedGeneralFixes.stream()
				.filter(saved -> saved.getRawId() != null && !masterRawIds.contains(saved.getRawId()))
				.collect(Collectors.toList());

		if (obsoleteRecords.isEmpty()) {
			return;
		}

		List<Long> generalFixIds = obsoleteRecords.stream().map(EventGeneralFixEntity::getId).filter(Objects::nonNull)
				.collect(Collectors.toList());

		// First delete child records
		if (!generalFixIds.isEmpty()) {
			eventFunctionGeneralFixRepository.deleteByEventGeneralfixIdIn(generalFixIds);
		}

		// Then delete parent records
		eventGeneralFixRepository.deleteAll(obsoleteRecords);
	}

//	private EventGeneralFixRawResponseDto buildRawResponse(Long generalFixId, RawMaterialMasterEntity rawMaterial,
//			List<EventFunctionMasterEntity> eventFunctions, Map<Long, EventFunctionMasterEntity> eventFunctionMap) {
//
//		EventGeneralFixRawResponseDto rawResponse = new EventGeneralFixRawResponseDto();
//
//		rawResponse.setId(generalFixId);
//
//		applyRawMaterial(rawResponse, rawMaterial);
//
//		if (rawMaterial.getRawMaterialCat() != null) {
//			applyRawCategory(rawResponse, rawMaterial.getRawMaterialCat());
//		}
//
//		applyUnit(rawResponse, rawMaterial.getUnit());
//
//		List<EventFunctionGeneralFixRawResponseDto> functionResponses = new ArrayList<>();
//
//		BigDecimal totalPrice = BigDecimal.ZERO;
//
//		Map<Long, BigDecimal> unitQtyMap = new HashMap<>();
//
//		for (EventFunctionMasterEntity eventFunction : eventFunctions) {
//
//			EventFunctionGeneralFixRawResponseDto functionResponse = buildFunctionRawResponse(rawMaterial,
//					eventFunction);
//
//			totalPrice = totalPrice.add(safe(functionResponse.getPrice()));
//
//			if (functionResponse.getUnit() != null && functionResponse.getWeight() != null) {
//
//				unitQtyMap.merge(functionResponse.getUnit().getId(), functionResponse.getWeight(),
//						BigDecimal::add);
//			}
//
//			functionResponses.add(functionResponse);
//		}
//
//		applyTotalWeight(rawResponse, unitQtyMap);
//
//		rawResponse.setPrice(removeDecimal(totalPrice));
//
//		rawResponse.setEventFunctionGeneralFixRaws(functionResponses);
//
//		return rawResponse;
//	}
//
//	private EventGeneralFixRawResponseDto buildGeneralFixResponse(EventGeneralFixEntity generalFix,
//			Map<Long, List<EventFunctionGeneralFixEntity>> functionFixMap,
//			Map<Long, EventFunctionMasterEntity> eventFunctionMap, Map<Long, RawMaterialMasterEntity> rawMaterialMap,
//			Map<Long, RawMaterialCategoryMasterEntity> rawCategoryMap, Map<Long, UnitMasterEntity> unitMap) {
//
//		EventGeneralFixRawResponseDto response = new EventGeneralFixRawResponseDto();
//
//		response.setId(generalFix.getId());
//
//		RawMaterialMasterEntity rawMaterial = rawMaterialMap.get(generalFix.getRawId());
//
//		RawMaterialCategoryMasterEntity rawCategory = rawCategoryMap.get(generalFix.getRawCatId());
//
//		UnitMasterEntity unit = unitMap.get(generalFix.getUnitId());
//
//		applyRawMaterial(response, rawMaterial);
//		applyRawCategory(response, rawCategory);
//		applyUnit(response, unit);
//
//		List<EventFunctionGeneralFixEntity> functionFixList = functionFixMap.getOrDefault(generalFix.getId(),
//				Collections.emptyList());
//
//		List<EventFunctionGeneralFixRawResponseDto> functionResponses = new ArrayList<>();
//
//		BigDecimal totalWeight = BigDecimal.ZERO;
//		BigDecimal totalPrice = BigDecimal.ZERO;
//
//		for (EventFunctionGeneralFixEntity functionFix : functionFixList) {
//
//			BigDecimal weight = safe(functionFix.getWeight());
//			BigDecimal price = safe(functionFix.getPrice());
//
//			EventFunctionGeneralFixRawResponseDto functionResponse = new EventFunctionGeneralFixRawResponseDto();
//
//			functionResponse.setId(functionFix.getId());
//			functionResponse.setWeight(weight);
//			functionResponse.setPrice(price);
//
//			applyRawMaterial(functionResponse, rawMaterial);
//			applyRawCategory(functionResponse, rawCategory);
//			applyUnit(functionResponse, unit);
//
//			applyEventFunction(functionResponse, eventFunctionMap.get(functionFix.getEventFunctionId()));
//
//			totalWeight = totalWeight.add(weight);
//			totalPrice = totalPrice.add(price);
//
//			functionResponses.add(functionResponse);
//		}
//
//		response.setWeight(totalWeight);
//		response.setPrice(totalPrice);
//		response.setEventFunctionGeneralFixRaws(functionResponses);
//
//		return response;
//	}

	private void applyUnit(EventGeneralFixRawResponseDto dto, UnitMasterEntity unit) {

		if (unit == null) {
			dto.setUnit(null);
			dto.setUnitHierarchyDto(null);
			return;
		}

		dto.setUnit(unitMasterService.getById(unit.getId()));

		dto.setUnitHierarchyDto(unitMasterService.getParentUnitsWithChildren(unit.getId()));
	}

	private GeneralFixRawResponseDto getFromRawMaterialMaster(Long eventId,
			List<EventFunctionMasterEntity> eventFunctions, List<Long> rawCatIds) {

		GeneralFixRawResponseDto response = new GeneralFixRawResponseDto();

		response.setEventId(eventId);

		List<RawMaterialMasterEntity> rawMaterials;

		// Filter by Raw Category if provided
		if (rawCatIds != null && !rawCatIds.isEmpty()) {

			rawMaterials = rawMaterialMasterRepository
					.findGeneralFixRawMaterialsByCategory(RAW_MATERIAL_CATEGORY_TYPE_ID, rawCatIds);

		} else {

			rawMaterials = rawMaterialMasterRepository.findGeneralFixRawMaterials(RAW_MATERIAL_CATEGORY_TYPE_ID);
		}

		// No raw materials found
		if (rawMaterials == null || rawMaterials.isEmpty()) {

			response.setEventGeneralFixRaws(Collections.emptyList());

			return response;
		}

		List<EventGeneralFixRawResponseDto> rawResponses = new ArrayList<>();

		for (RawMaterialMasterEntity rawMaterial : rawMaterials) {

			EventGeneralFixRawResponseDto rawResponse = buildRawResponse(null, // No EventGeneralFix ID before save
					rawMaterial, eventFunctions, Collections.emptyList());

			rawResponses.add(rawResponse);
		}

		response.setEventGeneralFixRaws(rawResponses);

		return response;
	}

//	private EventGeneralFixRawResponseDto buildRawMaterialResponse(RawMaterialMasterEntity rawMaterial,
//			List<EventFunctionMasterEntity> eventFunctions) {
//
//		EventGeneralFixRawResponseDto rawResponse = new EventGeneralFixRawResponseDto();
//
//		applyRawMaterial(rawResponse, rawMaterial);
//
//		if (rawMaterial.getRawMaterialCat() != null) {
//			applyRawCategory(rawResponse, rawMaterial.getRawMaterialCat());
//		}
//
//		applyUnit(rawResponse, rawMaterial.getUnit());
//
//		BigDecimal weightPer100Pax = safe(rawMaterial.getWeightPer100Pax());
//
//		BigDecimal supplierRate = safe(rawMaterial.getSupplierRate());
//
//		List<EventFunctionGeneralFixRawResponseDto> functionResponses = new ArrayList<>();
//
//		BigDecimal totalPrice = BigDecimal.ZERO;
//
//		Map<Long, BigDecimal> unitQtyMap = new HashMap<>();
//
//		for (EventFunctionMasterEntity eventFunction : eventFunctions) {
//
//			EventFunctionGeneralFixRawResponseDto functionResponse = buildFunctionRawResponse(rawMaterial,
//					eventFunction, weightPer100Pax, supplierRate);
//
//			totalPrice = totalPrice.add(safe(functionResponse.getPrice()));
//
//			if (functionResponse.getUnit() != null && functionResponse.getWeight() != null) {
//
//				unitQtyMap.merge(functionResponse.getUnit().getId(), functionResponse.getWeight(),
//						BigDecimal::add);
//				
//			}
//
//			functionResponses.add(functionResponse);
//		}
//
//		applyTotalWeight(rawResponse, unitQtyMap);
//
//		rawResponse.setPrice(removeDecimal(totalPrice));
//		rawResponse.setEventFunctionGeneralFixRaws(functionResponses);
//
//		return rawResponse;
//	}

	private void applyTotalWeight(EventGeneralFixRawResponseDto rawResponse, Map<Long, BigDecimal> unitQtyMap) {

		if (unitQtyMap == null || unitQtyMap.isEmpty()) {

			rawResponse.setWeight(BigDecimal.ZERO);
			return;
		}

		Map<Long, BigDecimal> parentUnitQtyMap = convertToParentUnitBigDecimal(unitQtyMap);

		if (parentUnitQtyMap == null || parentUnitQtyMap.isEmpty()) {

			rawResponse.setWeight(BigDecimal.ZERO);
			return;
		}

		if (parentUnitQtyMap.size() > 1) {

			throw new RuntimeException("Cannot calculate total weight because multiple incompatible units found");
		}

		Map.Entry<Long, BigDecimal> entry = parentUnitQtyMap.entrySet().iterator().next();

		Long unitId = entry.getKey();

		BigDecimal totalWeight = safe(entry.getValue());

		UnitMasterEntity unit = unitMasterRepository.findById(unitId).orElse(null);

		UnitConversionResult conversion = unitConversionService.autoConvert(unit, totalWeight);
		if (unit != null) {

			applyUnit(rawResponse, conversion.getUnit());
		}

		int decimalLimit = rawResponse.getUnit() != null && rawResponse.getUnit().getDecimalLimit() != null
				? rawResponse.getUnit().getDecimalLimit()
				: 2;
		rawResponse.setWeight(conversion.getQuantity().setScale(decimalLimit, RoundingMode.HALF_UP));
	}

	private Map<Long, BigDecimal> convertToParentUnitBigDecimal(Map<Long, BigDecimal> unitQtyMap) {

		if (unitQtyMap == null || unitQtyMap.isEmpty()) {

			return Collections.emptyMap();
		}

		Map<Long, BigDecimal> resultMap = new HashMap<>();

		for (Map.Entry<Long, BigDecimal> entry : unitQtyMap.entrySet()) {

			Long unitId = entry.getKey();

			BigDecimal quantity = safe(entry.getValue());

			Optional<UnitMasterEntity> unitOptional = unitMasterRepository.findById(unitId);

			if (!unitOptional.isPresent()) {

				resultMap.merge(unitId, quantity, BigDecimal::add);

				continue;
			}

			UnitMasterEntity unit = unitOptional.get();

			if (unit.getParentUnit() != null && unit.getParentUnit().getId() != null
					&& unit.getEquivalentValue() != null
					&& BigDecimal.valueOf(unit.getEquivalentValue()).compareTo(BigDecimal.ZERO) > 0) {

				Long parentUnitId = unit.getParentUnit().getId();

				BigDecimal convertedQuantity = quantity.divide(BigDecimal.valueOf(unit.getEquivalentValue()), 6,
						RoundingMode.HALF_UP);

				resultMap.merge(parentUnitId, convertedQuantity, BigDecimal::add);

			} else {

				resultMap.merge(unitId, quantity, BigDecimal::add);
			}
		}

		return resultMap;
	}

	private EventGeneralFixRawResponseDto buildRawResponse(Long generalFixId, RawMaterialMasterEntity rawMaterial,
			List<EventFunctionMasterEntity> eventFunctions, List<EventFunctionGeneralFixEntity> existingFunctionFixes) {

		EventGeneralFixRawResponseDto rawResponse = new EventGeneralFixRawResponseDto();
		rawResponse.setId(generalFixId);

		applyRawMaterial(rawResponse, rawMaterial);

		if (rawMaterial.getRawMaterialCat() != null) {
			applyRawCategory(rawResponse, rawMaterial.getRawMaterialCat());
		}
		if (rawMaterial.getUnit() != null) {
			applyUnit(rawResponse, rawMaterial.getUnit());
		}

		Map<Long, EventFunctionGeneralFixEntity> existingByFunctionId = existingFunctionFixes == null
				? Collections.emptyMap()
				: existingFunctionFixes.stream().collect(Collectors
						.toMap(EventFunctionGeneralFixEntity::getEventFunctionId, Function.identity(), (a, b) -> a));

		List<EventFunctionGeneralFixRawResponseDto> functionResponses = new ArrayList<>();
		BigDecimal totalPrice = BigDecimal.ZERO;
		Map<Long, BigDecimal> unitQtyMap = new HashMap<>();

		if (eventFunctions != null && !eventFunctions.isEmpty()) {
			for (EventFunctionMasterEntity eventFunction : eventFunctions) {

				EventFunctionGeneralFixEntity existing = existingByFunctionId.get(eventFunction.getId());

				EventFunctionGeneralFixRawResponseDto functionResponse = buildFunctionRawResponse(rawMaterial,
						eventFunction, existing);

				totalPrice = totalPrice.add(safe(functionResponse.getPrice()));

				if (functionResponse.getUnit() != null && functionResponse.getUnit().getId() != null
						&& functionResponse.getWeight() != null) {
					unitQtyMap.merge(functionResponse.getUnit().getId(), functionResponse.getWeight(), BigDecimal::add);
				}

				functionResponses.add(functionResponse);
			}
		}

		applyTotalWeight(rawResponse, unitQtyMap);
		rawResponse.setPrice(removeDecimal(totalPrice));
		rawResponse.setEventFunctionGeneralFixRaws(functionResponses);

		return rawResponse;
	}

	private EventFunctionGeneralFixRawResponseDto buildFunctionRawResponse(RawMaterialMasterEntity rawMaterial,
			EventFunctionMasterEntity eventFunction, EventFunctionGeneralFixEntity existing) {

		EventFunctionGeneralFixRawResponseDto response = new EventFunctionGeneralFixRawResponseDto();

		BigDecimal weightPer100Pax = safe(rawMaterial.getWeightPer100Pax());
		BigDecimal supplierRate = safe(rawMaterial.getSupplierRate());
		Long currentUnitId = rawMaterial.getUnit() != null ? rawMaterial.getUnit().getId() : null;
		Integer pax = eventFunction.getPax() != null ? eventFunction.getPax() : 0;

		BigDecimal calculatedWeight;
		BigDecimal calculatedPrice;
		UnitMasterEntity calculationUnit;
		System.out.println("existing:- " + existing);
		boolean rawMaterialChanged = existing != null
				&& hasRawMaterialChanged(existing, weightPer100Pax, supplierRate, currentUnitId);
		System.out.println("rawMaterialChanged:- " + rawMaterialChanged);
		if (existing != null && !rawMaterialChanged) {
			System.out.println("in 1");
			// Nothing changed upstream -> keep saved values AND the saved unit,
			// not whatever unit the raw material currently points to.
			calculatedWeight = safe(existing.getWeight());
			calculatedPrice = safe(existing.getPrice());

			calculationUnit = existing.getUnitId() != null
					? unitMasterRepository.findById(existing.getUnitId()).orElse(rawMaterial.getUnit())
					: rawMaterial.getUnit();

		} else {
			System.out.println("in 2");
			// Never saved, OR raw material's rate/qty/unit changed -> recalc fresh (sync)
			// using the raw material's CURRENT unit.
			calculatedWeight = weightPer100Pax.multiply(BigDecimal.valueOf(pax)).divide(BigDecimal.valueOf(100), 4,
					RoundingMode.HALF_UP);
			calculatedPrice = calculatedWeight.multiply(supplierRate);

			calculationUnit = rawMaterial.getUnit();
		}

		applyRawMaterial(response, rawMaterial);

		if (rawMaterial.getRawMaterialCat() != null) {
			applyRawCategory(response, rawMaterial.getRawMaterialCat());
		}

		applyEventFunction(response, eventFunction);

		applyCalculation(response, calculatedWeight, calculationUnit, calculatedPrice, rawMaterial.getIsApplyCal());

		response.setRawMaterialChanged(rawMaterialChanged);
		System.out.println("Weight:- " + response.getWeight());
		System.out.println("Price:- " + response.getPrice());
		return response;
	}

	private boolean hasRawMaterialChanged(EventFunctionGeneralFixEntity existing, BigDecimal currentWeightPer100Pax,
			BigDecimal currentSupplierRate, Long currentUnitId) {

		BigDecimal savedWeightPer100Pax = safe(existing.getSourceWeightPer100Pax());
		BigDecimal savedSupplierRate = safe(existing.getSourceSupplierRate());
		Long savedUnitId = existing.getSourceUnitId();

		boolean weightChanged = savedWeightPer100Pax.compareTo(safe(currentWeightPer100Pax)) != 0;
		boolean rateChanged = savedSupplierRate.compareTo(safe(currentSupplierRate)) != 0;
		boolean unitChanged = !Objects.equals(savedUnitId, currentUnitId);

		return weightChanged || rateChanged || unitChanged;
	}

//	private EventFunctionGeneralFixRawResponseDto buildFunctionRawResponse(RawMaterialMasterEntity rawMaterial,
//			EventFunctionMasterEntity eventFunction, BigDecimal weightPer100Pax, BigDecimal supplierRate) {
//
//		Integer pax = eventFunction.getPax() == null ? 0 : eventFunction.getPax();
//
//		BigDecimal calculatedWeight = weightPer100Pax.multiply(BigDecimal.valueOf(pax)).divide(BigDecimal.valueOf(100),
//				4, RoundingMode.HALF_UP);
//
//		BigDecimal calculatedPrice = calculatedWeight.multiply(supplierRate);
//
//		EventFunctionGeneralFixRawResponseDto response = new EventFunctionGeneralFixRawResponseDto();
//
//		applyRawMaterial(response, rawMaterial);
//
//		if (rawMaterial.getRawMaterialCat() != null) {
//			applyRawCategory(response, rawMaterial.getRawMaterialCat());
//		}
//
//		applyEventFunction(response, eventFunction);
//
//		applyCalculation(response, calculatedWeight, rawMaterial.getUnit(), calculatedPrice,
//				rawMaterial.getIsApplyCal());
//
//		return response;
//	}

	private void applyRawMaterial(EventGeneralFixRawResponseDto dto, RawMaterialMasterEntity rawMaterial) {

		if (rawMaterial == null) {
			return;
		}

		dto.setRawId(rawMaterial.getId());
		dto.setRawNameEnglish(rawMaterial.getNameEnglish());
		dto.setRawNameHindi(rawMaterial.getNameHindi());
		dto.setRawNameGujarati(rawMaterial.getNameGujarati());

		dto.setWeightPer100pax(safe(rawMaterial.getWeightPer100Pax()));
		dto.setSupplierRate(safe(rawMaterial.getSupplierRate()));
	}

	private void applyRawMaterial(EventFunctionGeneralFixRawResponseDto dto, RawMaterialMasterEntity rawMaterial) {

		if (rawMaterial == null) {
			return;
		}

		dto.setRawId(rawMaterial.getId());
		dto.setRawNameEnglish(rawMaterial.getNameEnglish());
		dto.setRawNameHindi(rawMaterial.getNameHindi());
		dto.setRawNameGujarati(rawMaterial.getNameGujarati());

		dto.setWeightPer100pax(safe(rawMaterial.getWeightPer100Pax()));
		dto.setSupplierRate(safe(rawMaterial.getSupplierRate()));
	}

	private void applyRawCategory(EventGeneralFixRawResponseDto dto, RawMaterialCategoryMasterEntity category) {

		if (category == null) {
			return;
		}

		dto.setRawCatId(category.getId());
		dto.setRawCatNameEnglish(category.getNameEnglish());
		dto.setRawCatNameHindi(category.getNameHindi());
		dto.setRawCatNameGujarati(category.getNameGujarati());
	}

	private void applyRawCategory(EventFunctionGeneralFixRawResponseDto dto, RawMaterialCategoryMasterEntity category) {

		if (category == null) {
			return;
		}

		dto.setRawCatId(category.getId());
		dto.setRawCatNameEnglish(category.getNameEnglish());
		dto.setRawCatNameHindi(category.getNameHindi());
		dto.setRawCatNameGujarati(category.getNameGujarati());
	}

	private void applyEventFunction(EventFunctionGeneralFixRawResponseDto dto,
			EventFunctionMasterEntity eventFunction) {

		if (eventFunction == null) {
			return;
		}

		dto.setEventFunctionId(eventFunction.getId());

		dto.setPax(eventFunction.getPax() != null ? eventFunction.getPax() : 0);

		if (eventFunction.getFunction() != null) {

			dto.setFunctionNameEnglish(eventFunction.getFunction().getNameEnglish());

			dto.setFunctionNameHindi(eventFunction.getFunction().getNameHindi());

			dto.setFunctionNameGujarati(eventFunction.getFunction().getNameGujarati());

		} else {

			dto.setFunctionNameEnglish("");
			dto.setFunctionNameHindi("");
			dto.setFunctionNameGujarati("");
		}
	}

	private void applyCalculation(EventFunctionGeneralFixRawResponseDto dto, BigDecimal weight, UnitMasterEntity unit,
			BigDecimal rate, Boolean isApplyCal) {
		BigDecimal finalWeight = BigDecimal.ZERO;
		if (isApplyCal) {
			finalWeight = roundOffUtility.applyUnitRange(weight, unit, null);
		} else {
			finalWeight = weight;
		}
		UnitConversionResult conversion = unitConversionService.autoConvert(unit, finalWeight);

		applyUnit(dto, conversion.getUnit());

		if (weight.compareTo(BigDecimal.ZERO) == 0) {
			dto.setPrice(removeDecimal(rate));
		} else {
			rate = safeMultiply(rate, finalWeight).divide(weight, 4, RoundingMode.HALF_UP);
			dto.setPrice(removeDecimal(rate));
		}

		dto.setWeight(conversion.getQuantity()
				.setScale(dto.getUnit() != null
						? (dto.getUnit().getDecimalLimit() == null ? 2 : dto.getUnit().getDecimalLimit())
						: 2, RoundingMode.HALF_UP));
	}

	private void applyUnit(EventFunctionGeneralFixRawResponseDto dto, UnitMasterEntity unit) {

		if (unit == null) {
			dto.setUnit(null);
			dto.setUnitHierarchyDto(null);
			return;
		}

		dto.setUnit(unitMasterService.getById(unit.getId()));

		dto.setUnitHierarchyDto(unitMasterService.getParentUnitsWithChildren(unit.getId()));
	}

	public static BigDecimal removeDecimal(BigDecimal value) {
		if (value == null) {
			return BigDecimal.ZERO;
		}
		return value.setScale(0, RoundingMode.DOWN);
	}

	private BigDecimal safeMultiply(BigDecimal a, BigDecimal b) {
		return safe(a).multiply(safe(b));
	}

	private BigDecimal safe(BigDecimal v) {
		return v == null ? BigDecimal.ZERO : v;
	}

	public Map<Long, Double> convertToParentUnit(Map<Long, Double> unitQtyMap) {

		if (unitQtyMap == null || unitQtyMap.isEmpty()) {
			return Collections.emptyMap();
		}

		if (unitQtyMap.size() == 1) {
			return new HashMap<>(unitQtyMap);
		}

		Map<Long, Double> resultMap = new HashMap<>();

		unitQtyMap.forEach((unitId, qty) -> {

			Optional<UnitMasterEntity> unitOp = unitMasterRepository.findByIdAndIsParentUnitFalse(unitId);

			if (unitOp.isPresent() && unitOp.get().getParentUnit() != null && unitOp.get().getEquivalentValue() != null
					&& unitOp.get().getEquivalentValue() != 0.0) {

				Long parentId = unitOp.get().getParentUnit().getId();

				Double convertedVal = convertUnitQtyToParentQty(unitOp.get(), qty);

				resultMap.merge(parentId, convertedVal, Double::sum);

			} else {
				resultMap.merge(unitId, qty, Double::sum);
			}
		});

		return resultMap;
	}

	private Double convertUnitQtyToParentQty(UnitMasterEntity unit, Double qty) {

		return BigDecimal.valueOf(qty).divide(BigDecimal.valueOf(unit.getEquivalentValue()), 2, RoundingMode.HALF_UP)
				.doubleValue();
	}
}
