package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventRawMaterialEntity;
import com.crmportal.entity.EventRawMaterialFunctions;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventRawMaterialFunctionsRepository;
import com.crmportal.repository.EventRawMaterialRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.request.dto.EventRawMaterialRequest;
import com.crmportal.response.dto.EventRawMaterialFunctionDeleteDto;
import com.crmportal.response.dto.EventRawMaterialFunctionsDto;
import com.crmportal.response.dto.EventRawMaterialQtySumDto;
import com.crmportal.response.dto.EventRawMaterialQueryResponse;
import com.crmportal.response.dto.EventRawMaterialResponse;
import com.crmportal.utility.RoundOffUtility;

@Service
public class EventRawMaterialHelperService {

	@Autowired
	private EventRawMaterialFunctionsRepository eventRawMaterialFunctionsRepository;

	@Autowired
	private EventRawMaterialRepository eventRawMaterialRepository;

	@Autowired
	private EventRawMaterialServiceImpl eventRawMaterialServiceImpl;

	@Autowired
	private UnitMasterRepository unitMasterRepository;

	@Autowired
	RoundOffUtility roundOffUtility;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;

	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Transactional
	public void deleteEventRawMaterialFunctions(Long eventId, Long eventFunctionId, List<Long> menuItemId) {
		System.out.println("inn  deleteEventRawMaterialFunctions");
		// 1️ Fetch records using native query
		List<Object[]> rows = eventRawMaterialFunctionsRepository.findForDelete(eventId, eventFunctionId, menuItemId);

		// 2️ Map to DTO list
		List<EventRawMaterialFunctionDeleteDto> deleteDtos = rows.stream()
				.map(r -> new EventRawMaterialFunctionDeleteDto(((Number) r[0]).longValue(), // eventRawMaterialFunctionId
						((Number) r[1]).longValue() // eventRawMaterialId
				)).collect(Collectors.toList());

		// 3️ Generate UNIQUE eventRawMaterial.id list
		Set<Long> uniqueEventRawMaterialIds = deleteDtos.stream()
				.map(EventRawMaterialFunctionDeleteDto::getEventRawMaterialId).collect(Collectors.toSet());

		// 4️ Delete records from EventRawMaterialFunctions entity
		List<Long> functionIdsToDelete = deleteDtos.stream()
				.map(EventRawMaterialFunctionDeleteDto::getEventRawMaterialFunctionId).collect(Collectors.toList());

		eventRawMaterialFunctionsRepository.deleteAllById(functionIdsToDelete);

		// return unique eventRawMaterial IDs (useful for further cleanup)
		updateQtyFromFunctions(uniqueEventRawMaterialIds);

	}

	@Transactional
	public void updateQtyFromFunctions(Set<Long> uniqueEventRawMaterialIds) {
		if (uniqueEventRawMaterialIds == null || uniqueEventRawMaterialIds.isEmpty()) {
			return;
		}

		// 1️ Get SUM(qty) for all raw materials in ONE query
		List<EventRawMaterialQtySumDto> qtySumList = eventRawMaterialFunctionsRepository
				.sumQtyByEventRawMaterialIds(uniqueEventRawMaterialIds);

		// 2️ Convert list to Map<eventRawMaterialId, totalQty>
		Map<Long, Double> qtySumMap = qtySumList.stream().collect(Collectors
				.toMap(EventRawMaterialQtySumDto::getEventRawMaterialId, EventRawMaterialQtySumDto::getTotalQty));

		List<EventRawMaterialEntity> rawMaterials = eventRawMaterialRepository.findByIdIn(uniqueEventRawMaterialIds);

		for (EventRawMaterialEntity rawMaterial : rawMaterials) {
			Long eventRawMaterialId = rawMaterial.getId();

			// No EventRawMaterialFunctions exist → DELETE row
			if (!qtySumMap.containsKey(eventRawMaterialId)) {
				eventRawMaterialRepository.delete(rawMaterial);
				continue;
			}

			List<EventRawMaterialFunctions> functions = eventRawMaterialFunctionsRepository
					.findByEventRawMaterial_Id(eventRawMaterialId);

			Map<Long, Double> unitQtyMap = functions.stream().filter(f -> f.getUnit() != null)
					.collect(Collectors.groupingBy(f -> f.getUnit().getId(),
							Collectors.summingDouble(f -> f.getQty() != null ? f.getQty() : 0.0)));

			Map<Long, Double> convertedUnitQtyMap = eventRawMaterialServiceImpl.convertToParentUnit(unitQtyMap);

			if (!convertedUnitQtyMap.isEmpty()) {
				Map.Entry<Long, Double> firstEntry = convertedUnitQtyMap.entrySet().iterator().next();

				Long key = firstEntry.getKey();
				Double value = firstEntry.getValue();

				Double total = functions.stream().map(EventRawMaterialFunctions::getPrice).filter(Objects::nonNull)
						.mapToDouble(Double::doubleValue).sum();

				EventRawMaterialResponse response = new EventRawMaterialResponse();
				response.setRawMaterialNameEng(rawMaterial.getRawMaterial().getNameEnglish());
				response.setIsApplyCal(rawMaterial.getRawMaterial().getIsApplyCal());
				eventRawMaterialServiceImpl.applyCalculation2(response, BigDecimal.valueOf(value),
						BigDecimal.valueOf(value), unitMasterRepository.findByIdAndIsDeleteFalseAndIsActiveTrue(key),
						BigDecimal.valueOf(rawMaterial.getTotalprice()));
				rawMaterial.setQty(response.getQty());
				rawMaterial.setFinalqty(response.getFinalQty());
				rawMaterial.setUnit(unitMasterRepository.getById(response.getUnitId()));
				rawMaterial.setTotalprice(total);

			}
		}

		// 5️ Batch save updated entities
		eventRawMaterialRepository.saveAll(
				rawMaterials.stream().filter(r -> qtySumMap.containsKey(r.getId())).collect(Collectors.toList()));
	}

	public void addEventRawMaterialFunctionsByMenuItemId(Long eventId, Long eventFunctionId, List<Long> menuItemId) {
		System.out.println("inn  addEventRawMaterialFunctionsByMenuItemId");
		addEventRawMaterialFunctionsByMenuItemId(eventId, eventFunctionId, menuItemId, true);
	}

	public void addEventRawMaterialFunctionsByMenuItemId(Long eventId, Long eventFunctionId, List<Long> menuItemId,
			boolean isFromMenuAllocation) {

		try {
			// Step 1: Safe delete
			try {
				deleteEventRawMaterialFunctions(eventId, eventFunctionId, menuItemId);
			} catch (Exception e) {
				System.err.println("Issue while deleting before add: " + e.getMessage());
			}

			// Step 2: Fetch data
			List<Object[]> resultList = new ArrayList<>();

			List<Object[]> resultList2 = new ArrayList<>();

			if (isFromMenuAllocation) {
				resultList = eventRawMaterialRepository.getMenuAllocationRawMaterialDataByMenuItemIds(eventId,
						eventFunctionId, menuItemId);

				// captain receipe
				resultList2 = eventRawMaterialRepository.getMenuAllocationCaptainReceipeRawMaterialDataByMenuItemIds(
						eventId, eventFunctionId, menuItemId);
				System.out.println("resultList2 size := " + resultList2.size());
			} else {
				resultList = eventRawMaterialRepository.getEventRawMaterialByMenuPreparation(eventId, eventFunctionId,
						menuItemId);

				// captain receipe
				resultList2 = eventRawMaterialRepository.getEventCaptainReceipeRawMaterialByMenuPreparation(eventId,
						eventFunctionId, menuItemId);
			}
			resultList.addAll(resultList2);

			if (resultList == null || resultList.isEmpty()) {
				System.out.println("No raw material data found.");
				return;
			}

			// Step 3: Convert response
			List<EventRawMaterialResponse> finalResponse = eventRawMaterialServiceImpl
					.generateEventRawMaterialResponseFromQueryData(resultList);

			if (finalResponse == null || finalResponse.isEmpty()) {
				System.out.println("No processed raw material response.");
				return;
			}

			// Step 4: Fetch existing raw materials
			List<EventRawMaterialEntity> rawMaterials = eventRawMaterialRepository.findByEvent_Id(eventId);

			Map<Long, EventRawMaterialEntity> rawMaterialMap = new HashMap<>();

			if (rawMaterials != null && !rawMaterials.isEmpty()) {
				rawMaterialMap = rawMaterials.stream()
						.filter(e -> e.getRawMaterial() != null && e.getRawMaterial().getId() != null)
						.collect(Collectors.toMap(e -> e.getRawMaterial().getId(), Function.identity(),
								(existing, duplicate) -> existing));
			}

			// Step 5: Fetch function safely
			Optional<EventFunctionMasterEntity> functionOpt = eventFunctionMasterRepository.findById(eventFunctionId);

			if (!functionOpt.isPresent()) {
				System.out.println("Invalid eventFunctionId: " + eventFunctionId);
				return;
			}

			EventFunctionMasterEntity function = functionOpt.get();

			List<EventRawMaterialResponse> newlyAddResponse = new ArrayList<>();
			Set<Long> uniqueEventRawMaterialIds = new HashSet<>();

			for (EventRawMaterialResponse response : finalResponse) {

				if (response == null)
					continue;

				Long rawMaterialId = response.getRawMaterialId();
				if (response.getRawMaterialNameEng().equalsIgnoreCase("PATRA NA VATA")) {
				}
				if (rawMaterialId != null && rawMaterialMap.containsKey(rawMaterialId)) {
					EventRawMaterialEntity entity = rawMaterialMap.get(rawMaterialId);

					if (response.getEventRawMaterialFunctions() != null) {
						convertEventRawMatFunRequest(response.getEventRawMaterialFunctions(), entity, function);
					}

					uniqueEventRawMaterialIds.add(entity.getId());
				} else {
					newlyAddResponse.add(response);
				}
			}

			if (!uniqueEventRawMaterialIds.isEmpty()) {
				updateQtyFromFunctions(uniqueEventRawMaterialIds);
			}

			if (!newlyAddResponse.isEmpty()) {
				EventRawMaterialRequest request = eventRawMaterialServiceImpl.convertToRequest(eventId, 0L,
						newlyAddResponse);
				eventRawMaterialServiceImpl.addOrUpdateEventRawMaterialCommon(request);
			}

		} catch (Exception e) {
			System.err.println("Failed to process event raw material: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private UnitMasterEntity getUnit(Long unitId, Map<Long, UnitMasterEntity> unitMap) {
		if (unitMap.containsKey(unitId)) {
			return unitMap.get(unitId);
		}
		UnitMasterEntity res = unitMasterRepository.findById(unitId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid unitId: " + unitId));
		unitMap.put(unitId, res);
		return res;
	}

	private PartyMasterEntity getSupplier(Long supplierId, Map<Long, PartyMasterEntity> supplierMap) {
		if (supplierMap.containsKey(supplierId)) {
			return supplierMap.get(supplierId);
		}
		PartyMasterEntity res = partyMasterRepository.findById(supplierId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid supplierId: " + supplierId));
		supplierMap.put(supplierId, res);
		return res;
	}

	public void convertEventRawMatFunRequest(List<EventRawMaterialFunctionsDto> eventRawMaterialFunctions,
			EventRawMaterialEntity eventRawMaterial, EventFunctionMasterEntity function) {
		List<EventRawMaterialFunctions> functions = new ArrayList<>();
		Map<Long, UnitMasterEntity> unitMap = new HashMap<>();
		Map<Long, PartyMasterEntity> supplierMap = new HashMap<>();
		for (EventRawMaterialFunctionsDto f : eventRawMaterialFunctions) {

			EventRawMaterialFunctions erf = new EventRawMaterialFunctions();
			erf.setEvent(eventRawMaterial.getEvent());
			erf.setEventRawMaterial(eventRawMaterial);
			erf.setRawMaterialCat(eventRawMaterial.getRawMaterialCat());
			erf.setEventFunction(function);
			erf.setFunction(function.getFunction());
			BigDecimal qty = f.getQty() == null ? BigDecimal.ZERO : BigDecimal.valueOf(f.getQty());

			if (eventRawMaterial.getRawMaterial().getIsApplyCal()) {

				UnitMasterEntity unit = null;
				if (f.getUnitId() != null) {
					unit = unitMasterRepository.findByIdAndIsDeleteFalse(f.getUnitId()).orElse(null);
				}

				double calculatedQty = roundOffUtility.applyUnitRange(qty, unit, null).doubleValue();

				erf.setQty(calculatedQty);

			} else {
				erf.setQty(qty.doubleValue());
			}
			erf.setItemName(f.getItemName());
			erf.setPlace(f.getPlace());
			erf.setPrice(f.getPrice() == null ? 0.0 : f.getPrice());
			erf.setMenuitemid(f.getMenuItemId());
			erf.setFunctiondatetime(f.getFunctiondatetime());
			erf.setIsExtraField(f.getIsExtraField());
			erf.setRawMaterialPrice(f.getRawMaterialPrice() != null ? f.getRawMaterialPrice() : 0.0);
			erf.setUnit(getUnit(f.getUnitId(), unitMap));

			if (f.getSupplierId() != null && f.getSupplierId() != 0) {
				erf.setSupplier(getSupplier(f.getSupplierId(), supplierMap));
			}

			functions.add(erf);
		}
		if (functions.size() != 0)
			eventRawMaterialFunctionsRepository.saveAll(functions);
	}

}
