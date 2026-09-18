package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.collections4.map.LinkedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.EventRawMaterialEntity;
import com.crmportal.entity.EventRawMaterialFunctions;
import com.crmportal.entity.ExtraEventRawMaterialEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.MenuItemRawMaterialEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.PurchaseOrderStoreDetailEntity;
import com.crmportal.entity.PurchaseOrderStoreEntity;
import com.crmportal.entity.RawMaterialCategoryMasterEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.StoreIssueReturnDetailEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.repository.EventExtraRawMaterialRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventFunctionMenuAllocationRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EventRawMaterialFunctionsRepository;
import com.crmportal.repository.EventRawMaterialRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.MenuItemRawMaterialRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.PurchaseOrderStoreDetailRepository;
import com.crmportal.repository.PurchaseOrderStoreRepository;
import com.crmportal.repository.RawMaterialCategoryMasterRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.StoreIssueReturnDetailRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.request.dto.EventRawMaterialAppRequest;
import com.crmportal.request.dto.EventRawMaterialAppRequestDetails;
import com.crmportal.request.dto.EventRawMaterialDetailRequest;
import com.crmportal.request.dto.EventRawMaterialFunctionRequestDto;
import com.crmportal.request.dto.EventRawMaterialFunctionsDetailRequest;
import com.crmportal.request.dto.EventRawMaterialRequest;
import com.crmportal.response.dto.AgencyMenuCategoryResponseDto;
import com.crmportal.response.dto.AgencyMenuItemResponseDto;
import com.crmportal.response.dto.AgencyRawMaterialCategoryResponseDto;
import com.crmportal.response.dto.AgencyRawMaterialItemsResponseDto;
import com.crmportal.response.dto.AgencyRawMaterialReportResponseDto;
import com.crmportal.response.dto.EventRawMaterialCategoryResponse;
import com.crmportal.response.dto.EventRawMaterialFunctionsDto;
import com.crmportal.response.dto.EventRawMaterialInfoDto;
import com.crmportal.response.dto.EventRawMaterialPartyDTO;
import com.crmportal.response.dto.EventRawMaterialQueryResponse;
import com.crmportal.response.dto.EventRawMaterialResponse;
import com.crmportal.response.dto.GeneralFixResponseDto;
import com.crmportal.response.dto.MenuAllocationItemRawMaterialResponseDto;
import com.crmportal.response.dto.RawMaterialMasterResponseDto;
import com.crmportal.response.dto.SupplierRawMaterialResponseDto;
import com.crmportal.response.dto.UnitConversionResult;
import com.crmportal.response.dto.UnitHierarchyDto;
import com.crmportal.response.dto.UnitMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventRawMaterialService;
import com.crmportal.service.UnitMasterService;
import com.crmportal.utility.RoundOffUtility;

@Service
public class EventRawMaterialServiceImpl implements EventRawMaterialService {

	private final CommonService commonService;

	@Autowired
	private EventRawMaterialRepository eventRawMaterialRepository;

	@Autowired
	private EventRawMaterialFunctionsRepository eventRawMaterialFunctionsRepository;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Autowired
	RawMaterialMasterRepository rawMaterialMasterRepository;

	@Autowired
	UnitMasterRepository unitMasterRepository;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;

	@Autowired
	UnitMasterService unitMasterService;

	@Autowired
	RoundOffUtility roundOffUtility;

	@Autowired
	UnitConversionServiceImpl unitConversionService;

	@Autowired
	EventExtraRawMaterialRepository eventExtraRawMaterialRepository;

	@Autowired
	EventFunctionMenuAllocationRepository eventFunctionMenuAllocationRepository;

	@Autowired
	MenuItemRawMaterialRepository menuItemRawMaterialRepository;

	@Autowired
	MenuItemMasterRepository menuItemMasterRepository;

	@Autowired
	RawMaterialCategoryMasterRepository rawMaterialCategoryMasterRepository;

	@Autowired
	PurchaseOrderStoreDetailRepository purchaseOrderStoreDetailRepository;
	
	@Autowired
	StoreIssueReturnDetailRepository storeIssueReturnDetailRepository;
	
	EventRawMaterialServiceImpl(CommonService commonService) {
		this.commonService = commonService;
	}

	private static final BigDecimal ZERO = BigDecimal.ZERO;
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

	@Override
	public List<EventRawMaterialFunctionsDto> getEventRawMaterialByEventRawMaterialId(Long eventId, Long rawMateriaId) {
		List<EventRawMaterialResponse> res = getEventRawMaterial(eventId, 0l, rawMateriaId);
		return res.size() != 0 ? res.get(0).getEventRawMaterialFunctions() : new ArrayList<>();
	}

	public void saveDefaultIfNotExists(Long eventId, Long rawMateriaCatlId) {
		try {
			List<EventRawMaterialResponse> rawMatList = new ArrayList<>();
			rawMatList = eventRawMaterialRepository.getEventRawMaterialDetails(eventId, rawMateriaCatlId);
			if (rawMatList.size() == 0) {
				List<EventRawMaterialResponse> response = getEventRawMaterialIfDoesNotExist(eventId, rawMateriaCatlId,
						0l);
				defaultSave(eventId, rawMateriaCatlId, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to save default data for event raw material ", e);
		}
	}

	public List<EventRawMaterialResponse> getEventRawMaterial(Long eventId, Long rawMateriaCatlId, Long rawMateriaId) {
		try {
			List<EventRawMaterialResponse> rawMatList = new ArrayList<>();
			if (rawMateriaId == 0) {
				System.err.println("in 1");
				rawMatList = eventRawMaterialRepository.getEventRawMaterialDetails(eventId, rawMateriaCatlId);
			} else {
				System.err.println("in 2");
				rawMatList = eventRawMaterialRepository.getEventRawMaterialDetailsByRawMaterialId(eventId,
						rawMateriaId);
			}
			if (rawMatList.size() == 0) {
				System.err.println("in 3");
				List<EventRawMaterialResponse> response = getEventRawMaterialIfDoesNotExist(eventId, rawMateriaCatlId,
						rawMateriaId);
				if (rawMateriaId == 0) {
					System.err.println("in 4");
					defaultSave(eventId, rawMateriaCatlId, response);
				}
				return response;
			}
			List<EventRawMaterialResponse> rawfinalList = new ArrayList<>();
			for (EventRawMaterialResponse rawMat : rawMatList) {
				System.out.println("raw name : " + rawMat.getRawMaterialNameEng());
				Long eventMaterialId = rawMat.getId();
				List<EventRawMaterialFunctionsDto> functions = eventRawMaterialFunctionsRepository
						.getEventRawMaterialFunctionsDetails(eventMaterialId);
				for (EventRawMaterialFunctionsDto f : functions) {

					applyCalculation(f, BigDecimal.valueOf(f.getQty()),
							unitMasterRepository.findByIdAndIsDeleteFalseAndIsActiveTrue(f.getUnitId()),
							BigDecimal.valueOf(f.getRawMaterialPrice()), rawMat.getIsApplyCal());

				}
				rawMat.setEventRawMaterialFunctions(functions);
				applyCalculation2(rawMat, BigDecimal.valueOf(rawMat.getQty()), BigDecimal.valueOf(rawMat.getFinalQty()),
						unitMasterRepository.findByIdAndIsDeleteFalseAndIsActiveTrue(rawMat.getUnitId()),
						BigDecimal.valueOf(rawMat.getTotalprice()));

				rawfinalList.add(rawMat);
			}
			List<ExtraEventRawMaterialEntity> eventRawMaterialEntity = eventExtraRawMaterialRepository
					.findAllByEventIdAndRawmaterialCatId(eventId, rawMateriaCatlId);
			if (!eventRawMaterialEntity.isEmpty()) {
				for (ExtraEventRawMaterialEntity extraEventRawMaterialEntity : eventRawMaterialEntity) {
					EventRawMaterialResponse rawMaterialResponse = new EventRawMaterialResponse();
					rawMaterialResponse.setEventId(eventId);
					rawMaterialResponse.setExtraItemName(extraEventRawMaterialEntity.getExtraRawmaterial());
					rawMaterialResponse.setIsApplyCal(false);
					rawMaterialResponse.setDate(extraEventRawMaterialEntity.getCreatedAt()
							.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss")));
					applyCalculation2(rawMaterialResponse, BigDecimal.valueOf(extraEventRawMaterialEntity.getQty()),
							BigDecimal.valueOf(extraEventRawMaterialEntity.getFinalqty()),
							unitMasterRepository
									.findByIdAndIsDeleteFalseAndIsActiveTrue(extraEventRawMaterialEntity.getUnitId()),
							BigDecimal.valueOf(extraEventRawMaterialEntity.getTotalprice()));

					rawMaterialResponse.setPlace(extraEventRawMaterialEntity.getPlace());
					rawMaterialResponse.setId(extraEventRawMaterialEntity.getId());
					rawMaterialResponse.setSupplierId(extraEventRawMaterialEntity.getPartyId());
					rawMaterialResponse.setSupplierName(extraEventRawMaterialEntity.getPartyId() == null ? null
							: partyMasterRepository.findByIdAndIsDeleteFalse(extraEventRawMaterialEntity.getPartyId())
									.get().getNameEnglish());
					rawfinalList.add(rawMaterialResponse);
				}
			}
			return rawfinalList;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get event raw material ", e);
		}
	}

	private void applyCalculation(EventRawMaterialFunctionsDto dto, BigDecimal weight, UnitMasterEntity unit,
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
			dto.setRawMaterialPrice(removeDecimal(rate).doubleValue());
		} else {
			rate = safeMultiply(rate, finalWeight).divide(weight, 4, RoundingMode.HALF_UP);
			dto.setRawMaterialPrice(removeDecimal(rate).doubleValue());
		}

		dto.setQty(conversion.getQuantity()
				.setScale(dto.getUnit() != null
						? (dto.getUnit().getDecimalLimit() == null ? 2 : dto.getUnit().getDecimalLimit())
						: 2, RoundingMode.HALF_UP)
				.doubleValue());
	}

	public void applyCalculation2(EventRawMaterialResponse dto, BigDecimal weight, BigDecimal weight2,
			UnitMasterEntity unit, BigDecimal rate) {

		BigDecimal finalWeight = BigDecimal.ZERO;
		BigDecimal finalWeight2 = BigDecimal.ZERO;
		if (dto.getIsApplyCal()) {
			finalWeight = roundOffUtility.applyUnitRange(weight, unit, null);
			finalWeight2 = roundOffUtility.applyUnitRange(weight2, unit, null);
		} else {
			finalWeight = weight;
			finalWeight2 = weight2;
		}

		UnitConversionResult conversion = unitConversionService.autoConvert(unit, finalWeight);
		UnitConversionResult conversion2 = unitConversionService.autoConvert(unit, finalWeight2);

		applyUnit2(dto, conversion2.getUnit());
		if (weight2.compareTo(BigDecimal.ZERO) == 0) {
			dto.setTotalprice(removeDecimal(rate).doubleValue());
		} else {
			rate = safeMultiply(rate, finalWeight2).divide(weight2, 4, RoundingMode.HALF_UP);
			dto.setTotalprice(removeDecimal(rate).doubleValue());
		}

		dto.setQty(conversion.getQuantity()
				.setScale(dto.getUnits() != null
						? (dto.getUnits().getDecimalLimit() == null ? 2 : dto.getUnits().getDecimalLimit())
						: 2, RoundingMode.HALF_UP)
				.doubleValue());

		dto.setFinalQty(conversion2.getQuantity()
				.setScale(dto.getUnits() != null
						? (dto.getUnits().getDecimalLimit() == null ? 2 : dto.getUnits().getDecimalLimit())
						: 2, RoundingMode.HALF_UP)
				.doubleValue());

	}

	private BigDecimal safeMultiply(BigDecimal a, BigDecimal b) {
		return safe(a).multiply(safe(b));
	}

	private BigDecimal safe(BigDecimal v) {
		return v == null ? BigDecimal.ZERO : v;
	}

	private void applyUnit(EventRawMaterialFunctionsDto dto, UnitMasterEntity unit) {
		if (unit == null) {
			dto.setUnit(null);
			dto.setUnitHierarchyDto(null);
		} else {
			dto.setUnitId(unit.getId());
			dto.setUnitName(unit.getNameEnglish());
			dto.setUnit(unitMasterService.getById(unit.getId()));
			dto.setUnitHierarchyDto(unitMasterService.getParentUnitsWithChildren(unit.getId()));
		}
	}

	private void applyUnit2(EventRawMaterialResponse dto, UnitMasterEntity unit) {
		if (unit == null) {
			dto.setUnits(null);
			dto.setUnitHierarchyDto(null);
		} else {
			dto.setUnitId(unit.getId());
			dto.setUnits(unitMasterService.getById(unit.getId()));
			dto.setUnitHierarchyDto(unitMasterService.getParentUnitsWithChildren(unit.getId()));
		}
	}

	public static BigDecimal removeDecimal(BigDecimal value) {
		if (value == null) {
			return BigDecimal.ZERO;
		}
		return value.setScale(0, RoundingMode.DOWN); // removes everything after "."
	}

	@Override
	public boolean addEventRawMaterialFunctions(@Valid EventRawMaterialFunctionRequestDto request) {
		try {
			EventMasterEntity e = eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId()).orElseThrow(
					() -> new RuntimeException("Event Master with this eventId not found: " + request.getEventId()));

			if (request.getRawMaterialId() == null) {
				if (request.getUnitId() == null) {
					throw new IllegalArgumentException("UnitId is required for extra raw material");
				}

				ExtraEventRawMaterialEntity extra = new ExtraEventRawMaterialEntity();
				extra.setEventFunctionId(request.getEventFunctionId());
				extra.setEventId(request.getEventId());
				extra.setExtraRawmaterial(request.getExtraItem());
				extra.setFinalqty(request.getFinalQty());
				extra.setQty(request.getQty());
				extra.setPlace(request.getPlace());
				extra.setTotalprice(request.getTotalprice());
				extra.setRawmaterialCatId(request.getRawMaterialCatId());
				extra.setUnitId(request.getUnitId());

				if (request.getSupplierId() != null && request.getSupplierId() != 0) {
					extra.setPartyId(request.getSupplierId());
				}
				eventExtraRawMaterialRepository.deleteByEventFunctionIdAndRawMaterialCatId(request.getEventFunctionId(),
						request.getRawMaterialCatId());
				eventExtraRawMaterialRepository.save(extra);
			} else {
				Optional<EventRawMaterialEntity> entity = eventRawMaterialRepository
						.findByEvent_IdAndRawMaterial_IdAndIsDeleteFalse(request.getEventId(),
								request.getRawMaterialId());
				if (entity.isPresent()) {
					EventRawMaterialEntity er = entity.get();
					er.setEvent(e);
					er.setSupplier(request.getSupplierId() != 0 ? partyMasterRepository.getById(request.getSupplierId())
							: null);
					er.setUnit(unitMasterRepository.getById(request.getUnitId()));
					er.setQty(request.getQty());
					er.setFinalqty(request.getFinalQty());
					er.setPlace(request.getPlace());
					er.setTotalprice(request.getTotalprice());
					eventRawMaterialRepository.save(er);
					eventRawMaterialFunctionsRepository.deleteByEventRawMaterialId(er.getId());
					List<EventRawMaterialFunctions> erfun = new ArrayList<>();
					for (EventRawMaterialFunctionsDetailRequest ef : request.getEventRawMatFunctions()) {
						EventRawMaterialFunctions erf = new EventRawMaterialFunctions();
						erf.setEvent(e);
						erf.setEventRawMaterial(er);
						erf.setRawMaterialCat(er.getRawMaterialCat());
						EventFunctionMasterEntity efmP = eventFunctionMasterRepository.getById(ef.getEventFunctionId());
						erf.setEventFunction(efmP);
						erf.setFunction(efmP.getFunction());
						erf.setQty(ef.getQty());
						erf.setMenuitemid(ef.getMenuItemId());
						erf.setItemName(ef.getItemName());
						erf.setSupplier(ef.getSupplierId() != null && ef.getSupplierId() != 0
								? partyMasterRepository.getById(ef.getSupplierId())
								: null);
						erf.setUnit(unitMasterRepository.getById(ef.getUnitId()));
						erf.setPlace(ef.getPlace());
						erf.setPrice(ef.getPrice());
						erf.setFunctiondatetime(ef.getFunctiondatetime());
						erf.setIsExtraField(ef.getIsExtraField());
						erf.setRawMaterialPrice(ef.getRawMaterialRate() != null ? ef.getRawMaterialRate() : 0.0);
						erfun.add(erf);
					}
					eventRawMaterialFunctionsRepository.saveAll(erfun);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to add/update event raw material functions", e);
		}

	}

	public void addOrUpdateEventRawMaterialCommon(EventRawMaterialRequest request) {
		EventMasterEntity event = eventMasterRepository.findById(request.getEventId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid eventId: " + request.getEventId()));
		for (EventRawMaterialDetailRequest raw : request.getEventRawMaterial()) {

			if (raw.getRawMaterialId() == null) {

				if (raw.getUnitId() == null) {
					throw new IllegalArgumentException("UnitId is required for extra raw material");
				}

				ExtraEventRawMaterialEntity extra = new ExtraEventRawMaterialEntity();
				System.out.println("extra Item:-" + raw.getExtraItem());
				extra.setEventFunctionId(request.getEventFunctionId());
				extra.setEventId(request.getEventId());
				extra.setExtraRawmaterial(raw.getExtraItem());
				extra.setFinalqty(raw.getFinalQty());
				extra.setQty(raw.getQty());
				extra.setPlace(raw.getPlace());
				extra.setTotalprice(raw.getTotalprice());
				extra.setRawmaterialCatId(request.getRawMaterialCategoryId());
				extra.setUnitId(raw.getUnitId());

				if (raw.getSupplierId() != null && raw.getSupplierId() != 0) {
					extra.setPartyId(raw.getSupplierId());
				}

				eventExtraRawMaterialRepository.save(extra);
				continue;
			}

			RawMaterialMasterEntity rawMaterial = rawMaterialMasterRepository.findById(raw.getRawMaterialId())
					.orElseThrow(
							() -> new IllegalArgumentException("Invalid rawMaterialId: " + raw.getRawMaterialId()));

			if (raw.getUnitId() == null) {
				throw new IllegalArgumentException("UnitId is required for raw material");
			}

			// Define formatter
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

			EventRawMaterialEntity er = new EventRawMaterialEntity();
			er.setEvent(event);
			er.setRawMaterial(rawMaterial);
			er.setRawMaterialCat(rawMaterial.getRawMaterialCat());
			er.setQty(raw.getQty());
			er.setFinalqty(raw.getFinalQty());
			er.setPlace(raw.getPlace());
			er.setRemarksEnglish(raw.getRemarksEnglish());
			er.setRemarksGujarati(raw.getRemarksGujarati());
			er.setRemarksHindi(raw.getRemarksHindi());
			System.out.println("RawMatId:- " + rawMaterial.getId() + " RawMatName:- " + rawMaterial.getNameEnglish());
			if (raw.getDate() != null) {
				er.setDelieveryDateTime(LocalDateTime.parse(raw.getDate(), formatter));
			}

			er.setTotalprice(raw.getTotalprice());

			er.setUnit(unitMasterRepository.findById(raw.getUnitId())
					.orElseThrow(() -> new IllegalArgumentException("Invalid unitId: " + raw.getUnitId())));

			if (raw.getSupplierId() != null && raw.getSupplierId() != 0) {
				er.setSupplier(partyMasterRepository.findById(raw.getSupplierId())
						.orElseThrow(() -> new IllegalArgumentException("Invalid supplierId: " + raw.getSupplierId())));
			}

			er = eventRawMaterialRepository.save(er);

			List<EventRawMaterialFunctions> functions = new ArrayList<>();

			for (EventRawMaterialFunctionsDetailRequest ef : raw.getEventRawMatFunctions()) {

				if (ef.getUnitId() == null) {
					throw new IllegalArgumentException("UnitId is required for function item");
				}

				EventFunctionMasterEntity function = eventFunctionMasterRepository.findById(ef.getEventFunctionId())
						.orElseThrow(() -> new IllegalArgumentException(
								"Invalid eventFunctionId: " + ef.getEventFunctionId()));

				EventRawMaterialFunctions erf = new EventRawMaterialFunctions();
				erf.setEvent(event);
				erf.setEventRawMaterial(er);
				erf.setRawMaterialCat(er.getRawMaterialCat());
				erf.setEventFunction(function);
				erf.setFunction(function.getFunction());
				erf.setQty(ef.getQty());
				erf.setItemName(ef.getItemName());
				erf.setPlace(ef.getPlace());
				erf.setPrice(ef.getPrice());
				erf.setMenuitemid(ef.getMenuItemId());
				erf.setFunctiondatetime(ef.getFunctiondatetime());
				erf.setIsExtraField(ef.getIsExtraField());
				erf.setRawMaterialPrice(ef.getRawMaterialRate() != null ? ef.getRawMaterialRate() : 0.0);
				erf.setUnit(unitMasterRepository.findById(ef.getUnitId())
						.orElseThrow(() -> new IllegalArgumentException("Invalid unitId: " + ef.getUnitId())));

				if (ef.getSupplierId() != null && ef.getSupplierId() != 0) {
					erf.setSupplier(partyMasterRepository.findById(ef.getSupplierId()).orElseThrow(
							() -> new IllegalArgumentException("Invalid supplierId: " + ef.getSupplierId())));
				}

				functions.add(erf);
			}

			eventRawMaterialFunctionsRepository.saveAll(functions);
		}
	}

	@Override
	public List<EventRawMaterialResponse> addOrUpdateEventRawMaterial(@Valid EventRawMaterialRequest request) {

		try {
			eventRawMaterialFunctionsRepository.deleteByEventIdAndRawMaterialCat(request.getEventId(),
					request.getRawMaterialCategoryId());

			eventRawMaterialRepository.deleteByEventIdAndRawMaterialCatId(request.getEventId(),
					request.getRawMaterialCategoryId());

			eventExtraRawMaterialRepository.deleteByEventFunctionIdAndRawMaterialCatId(request.getEventFunctionId(),
					request.getRawMaterialCategoryId());

			addOrUpdateEventRawMaterialCommon(request);

			return new ArrayList<>();

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Failed to add/update event raw material", ex);
		}
	}

	private UnitMasterResponseDto getUnit(Long unitId, Map<Long, UnitMasterResponseDto> unitMap) {
		try {
			if (unitMap.containsKey(unitId)) {
				return unitMap.get(unitId);
			}
			UnitMasterResponseDto res = unitMasterService.getById(unitId);
			unitMap.put(unitId, res);
			return res;
		} catch (RuntimeException e) {
			return unitMasterService.getById(unitId);
		}

	}

	public List<EventRawMaterialResponse> generateEventRawMaterialResponseFromQueryData(List<Object[]> resultList) {
		List<EventRawMaterialQueryResponse> matList = resultList.stream().map(obj -> {

			return new EventRawMaterialQueryResponse(toLong(obj[0]), // eventId
					toLong(obj[1]), // supplierId
					toString(obj[2]), // supplierName
					toLong(obj[3]), // rawMaterialId
					toString(obj[4]), // rawMaterialNameEng
					toString(obj[5]), // rawMaterialNameGuj
					toString(obj[6]), // rawMaterialNameHin
					toLong(obj[7]), // unitId
					toString(obj[8]), // unitName
					toDouble(obj[9]), // qty
					toDouble(obj[10]), // finalQty
					toString(obj[11]), // place
					toLong(obj[12]), // functionId
					toLong(obj[13]), // eventFunctionId
					toString(obj[14]), // functionName
					toString(obj[15]), // itemName
					toString(obj[16]), // function datetime
					toDouble(obj[17]), // totalPrice
					toDouble(obj[18]), // rawMaterialPrice
					toLong(obj[19]), toLong(obj[20]), toBoolean(obj[21]), "", "", "");
		}).collect(Collectors.toList());

		Map<Long, List<EventRawMaterialQueryResponse>> groupedMap = matList.stream()
				.filter(dto -> dto.getRawMaterialId() != null)
				.collect(Collectors.groupingBy(EventRawMaterialQueryResponse::getRawMaterialId));

		List<EventRawMaterialResponse> finalResponse = groupedMap.values().stream().map(list -> {
			// Pick the first item to populate static raw material-level fields
			EventRawMaterialQueryResponse first = list.get(0);

			// Build list of functions
			List<EventRawMaterialFunctionsDto> functionList = list.stream()
					.map(dto -> new EventRawMaterialFunctionsDto(dto.getFunctionId(), dto.getEventFunctionId(),
							dto.getFunctionName(), dto.getQty(), dto.getItemName(), dto.getSupplierId(),
							dto.getSupplierName(), dto.getUnitId(), dto.getUnitName(), dto.getPlace(),
							dto.getTotalPrice(), dto.getFunctiondatetime(), false, dto.getRawMaterialPrice(),
							dto.getMenuItemId()))
					.collect(Collectors.toList());

			Double total = list.stream().map(EventRawMaterialQueryResponse::getTotalPrice).filter(Objects::nonNull)
					.mapToDouble(Double::doubleValue).sum();

			Map<Long, Double> unitQtyMap = list.stream().filter(d -> d.getUnitId() != null)
					.collect(Collectors.groupingBy(EventRawMaterialQueryResponse::getUnitId,
							Collectors.summingDouble(d -> d.getQty() != null ? d.getQty() : 0.0)));

			Map<Long, Double> convertedUnitQtyMap = convertToParentUnit(unitQtyMap);
			Long key = first.getUnitId();
			Double value = list.stream().mapToDouble(d -> d.getQty() != null ? d.getQty() : 0.0).sum();
			if (!convertedUnitQtyMap.isEmpty()) {
				Map.Entry<Long, Double> firstEntry = convertedUnitQtyMap.entrySet().iterator().next();

				key = firstEntry.getKey();
				value = firstEntry.getValue();
			}

			// Build final response object
			EventRawMaterialResponse response = new EventRawMaterialResponse();
			response.setEventId(first.getEventId());
			response.setSupplierId(first.getSupplierId());
			response.setSupplierName(first.getSupplierName());
			response.setRawMaterialId(first.getRawMaterialId());
			response.setRawMaterialNameEng(first.getRawMaterialNameEng());
			response.setRawMaterialNameGuj(first.getRawMaterialNameGuj());
			response.setRawMaterialNameHin(first.getRawMaterialNameHin());
			response.setRawMaterialCatId(first.getRawMaterialCatId());
			response.setTotalprice(total);
			response.setIsApplyCal(first.getIsApplyCal());
			response.setRemarksEnglish(first.getRemarksEnglish());
			response.setRemarksGujarati(first.getRemarksGujarati());
			response.setRemarksHindi(first.getRemarksHindi());
			applyCalculation2(response, BigDecimal.valueOf(value), BigDecimal.valueOf(value),
					unitMasterRepository.findByIdAndIsDeleteFalseAndIsActiveTrue(key), BigDecimal.valueOf(
							list.stream().mapToDouble(d -> d.getTotalPrice() != null ? d.getTotalPrice() : 0.0).sum()));
			response.setPlace("At Venue");

			for (EventRawMaterialFunctionsDto eventRawMaterialFunctionsDto : functionList) {
				applyCalculation(eventRawMaterialFunctionsDto,
						BigDecimal.valueOf(eventRawMaterialFunctionsDto.getQty()),
						unitMasterRepository
								.findByIdAndIsDeleteFalseAndIsActiveTrue(eventRawMaterialFunctionsDto.getUnitId()),
						BigDecimal.valueOf(eventRawMaterialFunctionsDto.getRawMaterialPrice()),
						response.getIsApplyCal());
				eventRawMaterialFunctionsDto
						.setRawMaterialPrice(eventRawMaterialFunctionsDto.getRawMaterialPrice() != null
								? eventRawMaterialFunctionsDto.getRawMaterialPrice()
								: 0.0);
			}
			response.setEventRawMaterialFunctions(functionList);

			return response;
		}).collect(Collectors.toList());

		return finalResponse;
	}

	public static boolean toBoolean(Object value) {
		if (value == null)
			return false;

		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue() != 0;
		}

		if (value instanceof String) {
			String str = ((String) value).trim().toLowerCase();
			return str.equals("true") || str.equals("1") || str.equals("yes");
		}

		return false;
	}

	public List<EventRawMaterialResponse> getEventRawMaterialIfDoesNotExist(Long eventId, Long rawMateriaCatlId,
			Long rawMateriaId) {

		try {
			Map<Long, UnitMasterResponseDto> unitMap = new HashMap<>();

			List<Object[]> resultList = new ArrayList<>();
			if (rawMateriaId == 0) {
				resultList = eventRawMaterialRepository.getMenuAllocationRawMaterialData(eventId, rawMateriaCatlId);
			} else {
				resultList = eventRawMaterialRepository.getMenuAllocationRawMaterialDataByRawMaterialId(eventId,
						rawMateriaId);
			}
			boolean ifmenuallocation = eventFunctionMenuAllocationRepository.existsByEvent_Id(eventId);
			if (resultList.size() == 0 && !ifmenuallocation) {
				List<Object[]> resultList2 = new ArrayList<>();
				List<Long> eventFunctionIds =	eventRawMaterialRepository.findDistinctEventFunctionIdsByEventId(eventId);
				if (rawMateriaId == 0) {

					resultList = eventRawMaterialRepository.getEventRawMaterialIfDoesNotExist(eventId,
							rawMateriaCatlId,eventFunctionIds);

					// captain receipe
					resultList2 = eventRawMaterialRepository.getEventCaptainReceipeRawMaterailIfDoesNotExist(eventId,
							rawMateriaCatlId,eventFunctionIds);
				} else {
					resultList = eventRawMaterialRepository.getEventRawMaterialIfDoesNotExistByRawMaterialId(eventId,
							rawMateriaId,eventFunctionIds);

					// captain receipe
					resultList2 = eventRawMaterialRepository
							.getEventCaptainReceipeRawMaterialIfDoesNotExistByRawMaterialId(eventId, rawMateriaId,eventFunctionIds);
				}
				resultList.addAll(resultList2);

			}
			List<EventRawMaterialResponse> finalResponse = generateEventRawMaterialResponseFromQueryData(resultList);

			return finalResponse;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get event raw material ", e);
		}
	}

	public Map<Long, Double> convertToParentUnit(Map<Long, Double> unitQtyMap) {

		if (unitQtyMap == null || unitQtyMap.size() == 1) {
			return new HashMap<>(unitQtyMap);
		}

		Map<Long, Double> resultMap = new HashMap<>();

		unitQtyMap.forEach((unitId, qty) -> {

			Optional<UnitMasterEntity> unitOp = unitMasterRepository.findByIdAndIsParentUnitFalse(unitId);

			if (unitOp.isPresent() && unitOp.get().getParentUnit() != null && unitOp.get().getEquivalentValue() != null
					&& unitOp.get().getEquivalentValue() != 0.0) {

				Long parentId = unitOp.get().getParentUnit().getId();
				Double convertedVal = convertUnitQtyToParentQty(unitOp.get(), qty);

				// safely add or update
				resultMap.merge(parentId, convertedVal, Double::sum);

			} else {
				// keep original unit if no parent
				resultMap.merge(unitId, qty, Double::sum);
			}
		});

		return resultMap;
	}

	private Double convertUnitQtyToParentQty(UnitMasterEntity unit, Double qty) {

		return BigDecimal.valueOf(qty).divide(BigDecimal.valueOf(unit.getEquivalentValue()), 2, RoundingMode.HALF_UP)
				.doubleValue();
	}

	// --------------------
	// Safe Conversion Utils
	// --------------------
	private Long toLong(Object o) {
		if (o == null)
			return null;
		if (o instanceof Number)
			return ((Number) o).longValue();
		try {
			return Long.parseLong(o.toString());
		} catch (Exception e) {
			return null;
		}
	}

	private Double toDouble(Object o) {
		if (o == null)
			return null;
		if (o instanceof Number)
			return ((Number) o).doubleValue();
		try {
			return Double.parseDouble(o.toString());
		} catch (Exception e) {
			return null;
		}
	}

	private String toString(Object o) {
		return o == null ? "" : o.toString();
	}

	public static double roundToTwoDecimalDynamic(Double value) {
		if (value == null)
			return 0.0;

		return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().doubleValue();
	}

	public List<EventRawMaterialInfoDto> filterByRawMaterialId(List<EventRawMaterialInfoDto> rawMaterialDtos) {

		return rawMaterialDtos.stream().collect(Collectors.groupingBy(EventRawMaterialInfoDto::getRawMaterialId,
				LinkedHashMap::new, Collectors.toList())).values().stream().map(list -> {

					EventRawMaterialInfoDto first = list.get(0);

					// ---------------------------------------
					// Qty grouped by unit
					// ---------------------------------------
					Map<Long, Double> unitQtyMap = list.stream().filter(d -> d.getUnitId() != null)
							.collect(Collectors.groupingBy(EventRawMaterialInfoDto::getUnitId,
									Collectors.summingDouble(d -> d.getQty() == null ? 0.0 : d.getQty())));

					// Convert child units into parent unit
					Map<Long, Double> convertedQtyMap = convertToParentUnit(unitQtyMap);

					// Total Qty
					double totalQty = roundToTwoDecimalDynamic(
							convertedQtyMap.values().stream().mapToDouble(Double::doubleValue).sum());

					// ---------------------------------------
					// Final Qty
					// ---------------------------------------
					Map<Long, Double> finalQtyMap = list.stream().filter(d -> d.getUnitId() != null)
							.collect(Collectors.groupingBy(EventRawMaterialInfoDto::getUnitId,
									Collectors.summingDouble(d -> d.getFinalQty() == null ? 0.0 : d.getFinalQty())));

					Map<Long, Double> convertedFinalQtyMap = convertToParentUnit(finalQtyMap);

					double totalFinalQty = roundToTwoDecimalDynamic(
							convertedFinalQtyMap.values().stream().mapToDouble(Double::doubleValue).sum());

					// ---------------------------------------
					// Total Price
					// ---------------------------------------
					double totalPrice = roundToTwoDecimalDynamic(
							list.stream().mapToDouble(d -> d.getTotalPrice() == null ? 0.0 : d.getTotalPrice()).sum());

					// ---------------------------------------
					// Parent Unit
					// ---------------------------------------
					Long parentUnitId = null;
					String parentUnitName = "";

					if (!convertedQtyMap.isEmpty()) {

						parentUnitId = convertedQtyMap.keySet().iterator().next();

						UnitMasterEntity unit = unitMasterRepository.findById(parentUnitId).orElse(null);

						if (unit != null) {
							parentUnitName = unit.getSymbolEnglish();
						}
					}

					return new EventRawMaterialInfoDto(first.getEventRawMaterialId(), first.getRawMaterialId(),
							first.getRawMaterialNameEnglish(), first.getRawMaterialNameHindi(),
							first.getRawMaterialNameGujarati(), first.getSupplierId(), first.getSupplierName(),
							parentUnitId, parentUnitName, totalQty, totalFinalQty, first.getPlace(), totalPrice,
							first.getSequence());

				}).sorted(Comparator.comparing(d -> d.getSequence() == null ? 9999 : d.getSequence()))
				.collect(Collectors.toList());
	}

	public List<EventRawMaterialCategoryResponse> getEventRawMaterialByEventIdAndEventFunctionId(Long eventId,
			List<Long> eventFunctionIds, List<Long> rawMaterialCatIds) {
		List<EventRawMaterialFunctions> entities = eventRawMaterialFunctionsRepository
				.findByEventIdAndEventFunctionId(eventId, eventFunctionIds, rawMaterialCatIds);

		// 🔹 Group by Raw Material Category
		Map<RawMaterialCategoryMasterEntity, List<EventRawMaterialFunctions>> groupedByCategory = entities.stream()
				.collect(Collectors.groupingBy(e -> e.getRawMaterialCat(), LinkedHashMap::new, Collectors.toList()));

		List<EventRawMaterialCategoryResponse> response = new ArrayList<>();

		for (Map.Entry<RawMaterialCategoryMasterEntity, List<EventRawMaterialFunctions>> entry : groupedByCategory
				.entrySet()) {
			RawMaterialCategoryMasterEntity category = entry.getKey();
			List<EventRawMaterialFunctions> rawMaterialEntities = entry.getValue();

			List<EventRawMaterialInfoDto> rawMaterialDtos = rawMaterialEntities.stream().map(e -> {

				return new EventRawMaterialInfoDto(e.getId(), e.getEventRawMaterial().getRawMaterial().getId(),
						e.getEventRawMaterial().getRawMaterial().getNameEnglish(),
						e.getEventRawMaterial().getRawMaterial().getNameHindi(),
						e.getEventRawMaterial().getRawMaterial().getNameGujarati(),
						e.getSupplier() != null ? e.getSupplier().getId() : 0L,
						e.getSupplier() != null ? e.getSupplier().getNameEnglish() : "",
						e.getUnit() != null ? e.getUnit().getId() : null,
						e.getUnit() != null ? e.getUnit().getSymbolEnglish() : "", roundToTwoDecimalDynamic(e.getQty()),
						roundToTwoDecimalDynamic(e.getQty()), e.getPlace(), roundToTwoDecimalDynamic(e.getPrice()),
						e.getEventRawMaterial().getRawMaterial().getSequence());
			}).sorted(Comparator.comparing(d -> d.getSequence() == null ? 9999 : d.getSequence()))
					.collect(Collectors.toList());

			response.add(new EventRawMaterialCategoryResponse(category.getId(), category.getNameEnglish(),
					category.getNameHindi(), category.getNameGujarati(), filterByRawMaterialId(rawMaterialDtos)));
		}

		return response;
	}

	public List<EventRawMaterialCategoryResponse> getEventRawMaterialCrockerByEventIdAndEventFunctionId(Long eventId,
			Long eventFunctionId) {
		List<EventRawMaterialFunctions> entities = eventRawMaterialFunctionsRepository
				.findAllCrockeryByEventIdAndEventFunctionId(eventId, eventFunctionId);

		// 🔹 Group by Raw Material Category
		Map<RawMaterialCategoryMasterEntity, List<EventRawMaterialFunctions>> groupedByCategory = entities.stream()
				.collect(Collectors.groupingBy(e -> e.getRawMaterialCat()));

		List<EventRawMaterialCategoryResponse> response = new ArrayList<>();

		for (Map.Entry<RawMaterialCategoryMasterEntity, List<EventRawMaterialFunctions>> entry : groupedByCategory
				.entrySet()) {

			RawMaterialCategoryMasterEntity category = entry.getKey();
			List<EventRawMaterialFunctions> rawMaterialEntities = entry.getValue();

			List<EventRawMaterialInfoDto> rawMaterialDtos = rawMaterialEntities.stream()
					.map(e -> new EventRawMaterialInfoDto(e.getId(), e.getEventRawMaterial().getRawMaterial().getId(),
							e.getEventRawMaterial().getRawMaterial().getNameEnglish(),
							e.getEventRawMaterial().getRawMaterial().getNameHindi(),
							e.getEventRawMaterial().getRawMaterial().getNameGujarati(),
							e.getSupplier() != null ? e.getSupplier().getId() : 0L,
							e.getSupplier() != null ? e.getSupplier().getNameEnglish() : "",
							e.getUnit() != null ? e.getUnit().getId() : null,
							e.getUnit() != null ? e.getUnit().getSymbolEnglish() : "",
							roundToTwoDecimalDynamic(e.getQty()), roundToTwoDecimalDynamic(e.getQty()), e.getPlace(),
							roundToTwoDecimalDynamic(e.getPrice()),
							e.getEventRawMaterial().getRawMaterial().getSequence()))
					.collect(Collectors.toList());

			response.add(new EventRawMaterialCategoryResponse(category.getId(), category.getNameEnglish(),
					category.getNameHindi(), category.getNameGujarati(), filterByRawMaterialId(rawMaterialDtos)));
		}

		return response;
	}

//	@Override
//	public List<EventRawMaterialCategoryResponse> getEventRawMaterialByEventId(Long eventId,
//			List<Long> rawMaterialCatIds) {
//
//		List<EventRawMaterialEntity> entities = eventRawMaterialRepository.findAllByEventId(eventId, rawMaterialCatIds);
//
//		// 🔹 Group by Raw Material Category
//		Map<RawMaterialCategoryMasterEntity, List<EventRawMaterialEntity>> groupedByCategory = entities.stream()
//				.collect(Collectors.groupingBy(e -> e.getRawMaterial().getRawMaterialCat(), LinkedHashMap::new,
//						Collectors.toList()));
//
//		// 🔹 Build response
//		List<EventRawMaterialCategoryResponse> response = new ArrayList<>();
//
//		for (Map.Entry<RawMaterialCategoryMasterEntity, List<EventRawMaterialEntity>> entry : groupedByCategory
//				.entrySet()) {
//
//			RawMaterialCategoryMasterEntity category = entry.getKey();
//			List<EventRawMaterialEntity> rawMaterialEntities = entry.getValue();
//
//			List<EventRawMaterialInfoDto> rawMaterialDtos = rawMaterialEntities.stream()
//					.map(e -> new EventRawMaterialInfoDto(e.getId(), e.getRawMaterial().getId(),
//							e.getRawMaterial().getNameEnglish(), e.getRawMaterial().getNameHindi(),
//							e.getRawMaterial().getNameGujarati(),
//							e.getSupplier() != null ? e.getSupplier().getId() : 0L,
//							e.getSupplier() != null ? e.getSupplier().getNameEnglish() : "",
//							e.getUnit() != null ? e.getUnit().getId() : null,
//							e.getUnit() != null ? e.getUnit().getSymbolEnglish() : "", e.getQty(), e.getFinalqty(),
//							e.getPlace(), e.getTotalprice(), e.getRawMaterial().getSequence()))
//					.collect(Collectors.toList());
//
//			response.add(new EventRawMaterialCategoryResponse(category.getId(), category.getNameEnglish(),
//					category.getNameHindi(), category.getNameGujarati(), rawMaterialDtos));
//		}
//
//		return response;
//	}

	@Override
	public List<EventRawMaterialCategoryResponse> getEventRawMaterialByEventId(Long eventId,
			List<Long> rawMaterialCatIds, Integer isAddStoreIssue) {
		List<EventRawMaterialEntity> entities = eventRawMaterialRepository.findAllByEventId(eventId, rawMaterialCatIds);

		List<ExtraEventRawMaterialEntity> extraEntities = eventExtraRawMaterialRepository.findExtraRawMaterials(eventId,
				rawMaterialCatIds);

		List<PurchaseOrderStoreDetailEntity> storeIssues = purchaseOrderStoreDetailRepository.findAllByPo_EventId(eventId);
		
		// Category wise DTO map
		Map<Long, EventRawMaterialCategoryResponse> categoryMap = new LinkedHashMap<>();

		/*
		 * %%%%%%%%%%%%%%%%%%% Normal Raw Material %%%%%%%%%%%%%%%%%%%
		 */
		for (EventRawMaterialEntity e : entities) {

			RawMaterialCategoryMasterEntity category = e.getRawMaterial().getRawMaterialCat();

			EventRawMaterialCategoryResponse categoryResponse = categoryMap.computeIfAbsent(category.getId(),
					id -> new EventRawMaterialCategoryResponse(category.getId(), category.getNameEnglish(),
							category.getNameHindi(), category.getNameGujarati(), new ArrayList<>()));

			categoryResponse.getRawMaterials().add(new EventRawMaterialInfoDto(e.getId(), e.getRawMaterial().getId(),
					e.getRawMaterial().getNameEnglish(), e.getRawMaterial().getNameHindi(),
					e.getRawMaterial().getNameGujarati(), e.getSupplier() != null ? e.getSupplier().getId() : 0L,
					e.getSupplier() != null ? e.getSupplier().getNameEnglish() : "",
					e.getUnit() != null ? e.getUnit().getId() : null,
					e.getUnit() != null ? e.getUnit().getSymbolEnglish() : "", e.getQty(), e.getFinalqty(),
					e.getPlace(), e.getTotalprice(), e.getRawMaterial().getSequence()));
		}

		/*
		 * %%%%%%%%%%%%%%%%%%% Extra Raw Material %%%%%%%%%%%%%%%%%%%
		 */
		for (ExtraEventRawMaterialEntity e : extraEntities) {

			RawMaterialCategoryMasterEntity category = rawMaterialCategoryMasterRepository
					.findByIdAndIsDeleteFalse(e.getRawmaterialCatId()).orElse(null);

			if (category == null) {
				continue;
			}

			EventRawMaterialCategoryResponse categoryResponse = categoryMap.computeIfAbsent(category.getId(),
					id -> new EventRawMaterialCategoryResponse(category.getId(), category.getNameEnglish(),
							category.getNameHindi(), category.getNameGujarati(), new ArrayList<>()));

			PartyMasterEntity supplier = null;

			if (e.getPartyId() != null) {
				supplier = partyMasterRepository.findById(e.getPartyId()).orElse(null);
			}

			UnitMasterEntity unit = null;

			if (e.getUnitId() != null) {
				unit = unitMasterRepository.findById(e.getUnitId()).orElse(null);
			}

			categoryResponse.getRawMaterials().add(new EventRawMaterialInfoDto(e.getId(), 0L, // no raw material id
					e.getExtraRawmaterial(), "", "", supplier != null ? supplier.getId() : 0L,
					supplier != null ? supplier.getNameEnglish() : "", unit != null ? unit.getId() : null,
					unit != null ? unit.getSymbolEnglish() : "", e.getQty(), e.getFinalqty(), e.getPlace(),
					e.getTotalprice(), 999999 // sequence for extra items
			));
		}

		/*
		 * %%%%%%%%%%%%%%%%%%% Store Issue %%%%%%%%%%%%%%%%%%%
		 */
		if(isAddStoreIssue == 1 && storeIssues != null && !storeIssues.isEmpty()) {
			for (PurchaseOrderStoreDetailEntity issue : storeIssues) {
				RawMaterialCategoryMasterEntity category = rawMaterialCategoryMasterRepository
						.findByIdAndIsDeleteFalse(issue.getRawMaterialCat().getId()).orElse(null);
	
				if (category == null) {
					continue;
				}
				System.out.println("cat name : " + category.getNameEnglish());
	
				EventRawMaterialCategoryResponse categoryResponse = categoryMap.computeIfAbsent(category.getId(),
						id -> new EventRawMaterialCategoryResponse(category.getId(), category.getNameEnglish(),
								category.getNameHindi(), category.getNameGujarati(), new ArrayList<>()));
	
				UnitMasterEntity unit = null;
	
				if (issue.getUnit() != null) {
					unit = unitMasterRepository.findById(issue.getUnit().getId()).orElse(null);
				}
	
				 // Check if raw material already exists
		        Optional<EventRawMaterialInfoDto> existing = categoryResponse.getRawMaterials()
		                .stream()
		                .filter(rm -> rm.getRawMaterialId() != null
		                        && rm.getRawMaterialId().equals(issue.getRawMaterial().getId()))
		                .findFirst();
		        
		        if (existing.isPresent()) {
		            EventRawMaterialInfoDto dto = existing.get();

		            // Same unit
		            if (Objects.equals(dto.getUnitId(), issue.getUnit().getId())) {

		                dto.setQty(dto.getQty() + issue.getQty());
		                dto.setFinalQty(dto.getFinalQty() + issue.getQty());

		            } else {

		                // Different units -> convert to parent unit
		                Map<Long, Double> unitQtyMap = new LinkedHashMap<>();

		                unitQtyMap.put(dto.getUnitId(), dto.getQty());
		                unitQtyMap.merge(issue.getUnit().getId(), issue.getQty(), Double::sum);

		                Map<Long, Double> convertedMap = convertToParentUnit(unitQtyMap);

		                Map.Entry<Long, Double> entry = convertedMap.entrySet().iterator().next();

		                UnitMasterEntity u = unitMasterRepository.findById(entry.getKey()).orElse(null);

		                dto.setUnitId(entry.getKey());
		                dto.setUnitName(u != null ? u.getSymbolEnglish() : "");
		                dto.setQty(entry.getValue());
		                dto.setFinalQty(entry.getValue());
		            }


		        } else {
					categoryResponse.getRawMaterials().add(new EventRawMaterialInfoDto(issue.getId(), 0L, // no raw material id
							issue.getRawMaterial().getNameEnglish(), issue.getRawMaterial().getNameHindi(), issue.getRawMaterial().getNameGujarati(), 0L,
							"", unit != null ? unit.getId() : null,
							unit != null ? unit.getSymbolEnglish() : "", issue.getQty(), issue.getQty(), "",
							0d, 999999 // sequence for extra items
					));
		        }
			}
		}
		
		List<StoreIssueReturnDetailEntity> storeIssueReturns = storeIssueReturnDetailRepository
		        .findAllByStoreIssueReturn_EventId(eventId);

		/*
		 * %%%%%%%%%%%%%%%%%%% Store Issue Return %%%%%%%%%%%%%%%%%%%
		 */
		if (isAddStoreIssue == 1 && storeIssueReturns != null && !storeIssueReturns.isEmpty()) {
		    for (StoreIssueReturnDetailEntity ret : storeIssueReturns) {
		        RawMaterialCategoryMasterEntity category = rawMaterialCategoryMasterRepository
		                .findByIdAndIsDeleteFalse(ret.getRawMaterialCat().getId()).orElse(null);

		        if (category == null) {
		            continue;
		        }

		        EventRawMaterialCategoryResponse categoryResponse = categoryMap.get(category.getId());

		        if (categoryResponse == null) {
		            System.out.println("Return found with no matching issue category, raw material id: "
		                    + ret.getRawMaterial().getId());
		            continue;
		        }

		        Optional<EventRawMaterialInfoDto> existing = categoryResponse.getRawMaterials()
		                .stream()
		                .filter(rm -> rm.getRawMaterialId() != null
		                        && rm.getRawMaterialId().equals(ret.getRawMaterial().getId()))
		                .findFirst();

		        if (!existing.isPresent()) {
		            continue;
		        }

		        EventRawMaterialInfoDto dto = existing.get();

		        if (Objects.equals(dto.getUnitId(), ret.getUnit().getId())) {

		            dto.setQty(dto.getQty() - ret.getQty());
		            dto.setFinalQty(dto.getFinalQty() - ret.getQty());

		        } else {
		            Map<Long, Double> unitQtyMap = new LinkedHashMap<>();

		            unitQtyMap.put(dto.getUnitId(), dto.getQty());
		            unitQtyMap.merge(ret.getUnit().getId(), -ret.getQty(), Double::sum);

		            Map<Long, Double> convertedMap = convertToParentUnit(unitQtyMap);

		            Map.Entry<Long, Double> entry = convertedMap.entrySet().iterator().next();

		            UnitMasterEntity u = unitMasterRepository.findById(entry.getKey()).orElse(null);

		            dto.setUnitId(entry.getKey());
		            dto.setUnitName(u != null ? u.getSymbolEnglish() : "");
		            dto.setQty(entry.getValue());
		            dto.setFinalQty(entry.getValue());
		        }
		    }
		}
		
		// Optional sorting
		categoryMap.values().forEach(cat -> cat.getRawMaterials().sort(
				Comparator.comparing(EventRawMaterialInfoDto::getSequence, Comparator.nullsLast(Integer::compareTo))));

		return new ArrayList<>(categoryMap.values());
	}

	@Override
	public List<EventRawMaterialCategoryResponse> getEventRawMaterialCrockeryByEventId(Long eventId) {

		List<EventRawMaterialEntity> entities = eventRawMaterialRepository.findAllCrockeryByEventId(eventId);

		// 🔹 Group by Raw Material Category
		Map<RawMaterialCategoryMasterEntity, List<EventRawMaterialEntity>> groupedByCategory = entities.stream()
				.collect(Collectors.groupingBy(e -> e.getRawMaterial().getRawMaterialCat()));

		// 🔹 Build response
		List<EventRawMaterialCategoryResponse> response = new ArrayList<>();

		for (Map.Entry<RawMaterialCategoryMasterEntity, List<EventRawMaterialEntity>> entry : groupedByCategory
				.entrySet()) {

			RawMaterialCategoryMasterEntity category = entry.getKey();
			List<EventRawMaterialEntity> rawMaterialEntities = entry.getValue();

			List<EventRawMaterialInfoDto> rawMaterialDtos = rawMaterialEntities.stream()
					.map(e -> new EventRawMaterialInfoDto(e.getId(), e.getRawMaterial().getId(),
							e.getRawMaterial().getNameEnglish(), e.getRawMaterial().getNameHindi(),
							e.getRawMaterial().getNameGujarati(),
							e.getSupplier() != null ? e.getSupplier().getId() : 0L,
							e.getSupplier() != null ? e.getSupplier().getNameEnglish() : "",
							e.getUnit() != null ? e.getUnit().getId() : null,
							e.getUnit() != null ? e.getUnit().getSymbolEnglish() : "", e.getQty(), e.getFinalqty(),
							e.getPlace(), e.getTotalprice(), e.getRawMaterial().getSequence()))
					.collect(Collectors.toList());

			response.add(new EventRawMaterialCategoryResponse(category.getId(), category.getNameEnglish(),
					category.getNameHindi(), category.getNameGujarati(), rawMaterialDtos));
		}

		return response;
	}

//	public List<AgencyRawMaterialReportResponseDto> mapAgencyRawMaterialReport(Long eventId, Integer lang) {
//
//		List<Object[]> rows = eventRawMaterialRepository.findRawMaterialDetailsByEventAndParty(eventId, lang);
//		if (rows == null || rows.isEmpty()) {
//			rows = eventRawMaterialRepository.findSynchrnonizedRawMaterialDetailsByEventAndParty(eventId, lang);
//		}
//		
//		if(rows == null || rows.isEmpty()) {
//			return Collections.emptyList();
//		}
//		// ================= PARTY LEVEL =================
//		Map<Long, AgencyRawMaterialReportResponseDto> agencyMap = new LinkedHashMap<>();
//		
//		// ================= RAW MATERIAL AGGREGATION =================
//		Map<Long, Map<Long, AgencyRawMaterialItemsResponseDto>> agencyRawItemMap = new HashMap<>();
//		Map<Long, UnitMasterEntity> unitCache = new HashMap<>();
//		for (Object[] r : rows) {
//			// ---------- PARTY ----------
//			Long agencyId = ((Number) r[1]).longValue();
//
//			AgencyRawMaterialReportResponseDto agency = agencyMap.computeIfAbsent(agencyId, id -> {
//				AgencyRawMaterialReportResponseDto a = new AgencyRawMaterialReportResponseDto();
//				a.setAgencyId(agencyId);
//				a.setAgencyName((String) r[2]);
//				a.setMenuCategories(new ArrayList<>());
//				a.setRawMaterials(new ArrayList<>());
//				return a;
//			});
//			
//			// ================= MENU CATEGORY =================
//			Long menuCatId = ((Number) r[7]).longValue();
//
//			AgencyMenuCategoryResponseDto menuCat = agency.getMenuCategories().stream()
//					.filter(mc -> mc.getMenuCatId().equals(menuCatId)).findFirst().orElseGet(() -> {
//						AgencyMenuCategoryResponseDto mc = new AgencyMenuCategoryResponseDto();
//						mc.setMenuCatId(menuCatId);
//						mc.setMenuCatNameEnglish((String) r[8]);
//						mc.setMenuCatNameHindi((String) r[9]);
//						mc.setMenuCatNameGujarati((String) r[10]);
//						mc.setMenuItems(new ArrayList<>());
//						agency.getMenuCategories().add(mc);
//						return mc;
//					});
//			
//			// ================= MENU ITEM =================
//			Long menuItemId = ((Number) r[3]).longValue();
//
//			boolean itemExists = menuCat.getMenuItems().stream().anyMatch(i -> i.getMenuItemId().equals(menuItemId));
//			if (!itemExists) {
//				AgencyMenuItemResponseDto item = new AgencyMenuItemResponseDto();
//				item.setMenuItemId(menuItemId);
//				item.setMenuItemNameEnglish((String) r[4]);
//				item.setMenuItemNameHindi((String) r[5]);
//				item.setMenuItemNameGujarati((String) r[6]);
//				item.setPax(r[22] == null ? null : ((Number) r[22]).intValue());
//				menuCat.getMenuItems().add(item);
//			}
//			
//			// ================= RAW MATERIAL (AGGREGATE AGENCY LEVEL) =================
//			if (r[11] == null || r[15] == null) {
//				continue;
//			}
//			Long rawCatId = ((Number) r[11]).longValue();
//			Long rawItemId = ((Number) r[15]).longValue();
//			Long unitId = r[23] == null ? null : ((Number) r[23]).longValue();
//	        Double qty = r[19] == null ? 0.0 : ((Number) r[19]).doubleValue();
//
//			agencyRawItemMap.putIfAbsent(agencyId, new HashMap<>());
//			Map<Long, AgencyRawMaterialItemsResponseDto> rawItemMap = agencyRawItemMap.get(agencyId);
//			
//			AgencyRawMaterialItemsResponseDto rawItem = rawItemMap.computeIfAbsent(rawItemId, id -> {
//				AgencyRawMaterialItemsResponseDto ri = new AgencyRawMaterialItemsResponseDto();
//				ri.setRawItemId(rawItemId);
//				ri.setRawItemNameEnglish((String) r[16]);
//				ri.setRawItemNameHindi((String) r[17]);
//				ri.setRawItemNameGujarati((String) r[18]);
//				ri.setUnitName((String) r[21]);
//				ri.setUnitId(((BigInteger) r[23]).longValue());
//				ri.setTotalWeight(0.0);
//				ri.setUnitQtyMap(new HashMap<>());
//				return ri;
//			});
//			
//			if (unitId != null) {
//	            rawItem.getUnitQtyMap().merge(unitId, qty, Double::sum);
//	        }
//
////			if (r[19] != null) {
////				rawItem.setTotalWeight(rawItem.getTotalWeight() + ((Number) r[19]).doubleValue());
////			}
//			
//			// ================= RAW CATEGORY =================
//			AgencyRawMaterialCategoryResponseDto rawCat = agency.getRawMaterials().stream()
//					.filter(rc -> rc.getRawCatId().equals(rawCatId)).findFirst().orElseGet(() -> {
//						AgencyRawMaterialCategoryResponseDto rc = new AgencyRawMaterialCategoryResponseDto();
//						rc.setRawCatId(rawCatId);
//						rc.setRawCatNameEnglish((String) r[12]);
//						rc.setRawCatNameHindi((String) r[13]);
//						rc.setRawCatNameGujarati((String) r[14]);
//						rc.setRawItems(new ArrayList<>());
//						agency.getRawMaterials().add(rc);
//						return rc;
//					});
//			
//			if (rawCat.getRawItems().stream().noneMatch(i -> i.getRawItemId().equals(rawItemId))) {
//				rawCat.getRawItems().add(rawItem);
//			}
//			
//			agencyMap.values().forEach(a -> {
//		        a.getRawMaterials().forEach(cat -> {
//		            cat.getRawItems().forEach(item -> {
//
//		                Map<Long, Double> converted = convertToParentUnit(item.getUnitQtyMap());
//
//		                Long finalUnit = item.getUnitId();
//		                Double finalQty = item.getUnitQtyMap().values().stream().mapToDouble(Double::doubleValue).sum();
//
//		                if (!converted.isEmpty()) {
//		                    Map.Entry<Long, Double> entry = converted.entrySet().iterator().next();
//		                    finalUnit = entry.getKey();
//		                    finalQty = entry.getValue();
//		                }
//		                
//		                System.out.println("before conversion : " + finalQty);
//		                UnitMasterEntity u = unitMasterRepository.findByIdAndIsDeleteFalseAndIsActiveTrue(finalUnit);
//		                BigDecimal finalWeight = roundOffUtility.applyUnitRange(BigDecimal.valueOf(finalQty), u);
//		                System.out.println("after apply range : " + finalWeight);
//		                UnitConversionResult result = unitConversionService.autoConvert(u, finalWeight);
//		                System.out.println("after conversion : " + result.getQuantity());
//		                
//		                item.setUnitId(result.getUnit().getId());
//		                item.setTotalWeight(result.getQuantity().doubleValue());
//
//		                // optional → fetch correct unit name
////		                UnitMasterEntity unit =
////		                        unitCache.computeIfAbsent(finalUnit,
////		                                id -> unitMasterRepository
////		                                        .findByIdAndIsDeleteFalseAndIsActiveTrue(id));
//
//
//		                    String unitName;
//
//		                    if (lang == 1) {
//		                        unitName = result.getUnit().getNameHindi();
//		                    } else if (lang == 2) {
//		                        unitName = result.getUnit().getNameGujarati();
//		                    } else {
//		                        unitName = result.getUnit().getNameEnglish();
//		                    }
//
////		                    if (unitName == null) {
////		                        unitName = unit.getNameEnglish();
////		                    }
//
//		                    item.setUnitName(unitName);
//		            });
//		        });
//		    });
//			
//		}
//		return new ArrayList<>(agencyMap.values());
//	}

	public List<AgencyRawMaterialReportResponseDto> mapAgencyRawMaterialReport(Long eventId, Integer lang, List<Long> agencyIds, List<Long> itemId) {

		Boolean isAgency = true;
		if(agencyIds == null || agencyIds.isEmpty()) {
			isAgency = false;
		}
		
		Boolean isItem = true;
		if(itemId == null || itemId.isEmpty()) {
			isItem = false;
		}
		List<Object[]> rows = eventRawMaterialRepository.findRawMaterialDetailsByEventAndParty(eventId, lang, agencyIds, itemId, isAgency, isItem);

//		if (rows == null || rows.isEmpty()) {
//			rows = eventRawMaterialRepository.findSynchrnonizedRawMaterialDetailsByEventAndParty(eventId, lang);
//		}

		if (rows == null || rows.isEmpty()) {
			return Collections.emptyList();
		}

		// ================= ROOT MAP =================
		Map<Long, AgencyRawMaterialReportResponseDto> agencyMap = new LinkedHashMap<>();

		// FAST LOOKUPS
		Map<Long, Map<Long, AgencyMenuCategoryResponseDto>> agencyMenuCatMap = new HashMap<>();
		Map<Long, Map<Long, AgencyRawMaterialCategoryResponseDto>> agencyRawCatMap = new HashMap<>();
		Map<Long, Map<Long, AgencyRawMaterialItemsResponseDto>> agencyRawItemMap = new HashMap<>();

		// Collect unitIds for BULK fetch
		Set<Long> unitIds = new HashSet<>();

		// =====================================================
		// PHASE 1 → BUILD DATA (NO conversions here)
		// =====================================================

		for (Object[] r : rows) {

			Long agencyId = ((Number) r[1]).longValue();

			AgencyRawMaterialReportResponseDto agency = agencyMap.computeIfAbsent(agencyId, id -> {
				AgencyRawMaterialReportResponseDto a = new AgencyRawMaterialReportResponseDto();
				a.setAgencyId(id);
				a.setAgencyName((String) r[2]);
				a.setMenuCategories(new ArrayList<>());
				a.setRawMaterials(new ArrayList<>());
				return a;
			});

			// ================= MENU CATEGORY =================

			Long menuCatId = ((Number) r[7]).longValue();

			agencyMenuCatMap.putIfAbsent(agencyId, new HashMap<>());

			AgencyMenuCategoryResponseDto menuCat = agencyMenuCatMap.get(agencyId).computeIfAbsent(menuCatId, id -> {

				AgencyMenuCategoryResponseDto mc = new AgencyMenuCategoryResponseDto();

				mc.setMenuCatId(id);
				mc.setMenuCatNameEnglish((String) r[8]);
				mc.setMenuCatNameHindi((String) r[9]);
				mc.setMenuCatNameGujarati((String) r[10]);
				mc.setMenuItems(new ArrayList<>());

				agency.getMenuCategories().add(mc);

				return mc;
			});

			// ================= MENU ITEM =================

			Long menuItemId = ((Number) r[3]).longValue();

			// Avoid scanning list → use set trick
			if (menuCat.getMenuItems().stream().noneMatch(i -> i.getMenuItemId().equals(menuItemId))) {

				AgencyMenuItemResponseDto item = new AgencyMenuItemResponseDto();
				item.setMenuItemId(menuItemId);
				item.setMenuItemNameEnglish((String) r[4]);
				item.setMenuItemNameHindi((String) r[5]);
				item.setMenuItemNameGujarati((String) r[6]);
				item.setPax(r[22] == null ? null : ((Number) r[22]).intValue());

				menuCat.getMenuItems().add(item);
			}

			// ================= RAW MATERIAL =================

			if (r[11] == null || r[15] == null) {
				continue;
			}

			Long rawCatId = ((Number) r[11]).longValue();
			Long rawItemId = ((Number) r[15]).longValue();
			Long unitId = r[23] == null ? null : ((Number) r[23]).longValue();

			double qty = r[19] == null ? 0.0 : ((Number) r[19]).doubleValue();

			if (unitId != null) {
				unitIds.add(unitId);
			}

			// RAW CATEGORY MAP
			agencyRawCatMap.putIfAbsent(agencyId, new HashMap<>());

			AgencyRawMaterialCategoryResponseDto rawCat = agencyRawCatMap.get(agencyId).computeIfAbsent(rawCatId,
					id -> {

						AgencyRawMaterialCategoryResponseDto rc = new AgencyRawMaterialCategoryResponseDto();

						rc.setRawCatId(id);
						rc.setRawCatNameEnglish((String) r[12]);
						rc.setRawCatNameHindi((String) r[13]);
						rc.setRawCatNameGujarati((String) r[14]);
						rc.setRawItems(new ArrayList<>());

						agency.getRawMaterials().add(rc);

						return rc;
					});

			// RAW ITEM MAP
			agencyRawItemMap.putIfAbsent(agencyId, new HashMap<>());

			AgencyRawMaterialItemsResponseDto rawItem = agencyRawItemMap.get(agencyId).computeIfAbsent(rawItemId,
					id -> {

						AgencyRawMaterialItemsResponseDto ri = new AgencyRawMaterialItemsResponseDto();

						ri.setRawItemId(id);
						ri.setRawItemNameEnglish((String) r[16]);
						ri.setRawItemNameHindi((String) r[17]);
						ri.setRawItemNameGujarati((String) r[18]);
						ri.setUnitId(unitId);
						ri.setUnitQtyMap(new HashMap<>());
						ri.setTotalWeight(0.0);
						ri.setIsApplyCal((Boolean) r[24]);
						rawCat.getRawItems().add(ri);

						return ri;
					});

			if (unitId != null) {
				rawItem.getUnitQtyMap().merge(unitId, qty, Double::sum);
			}
		}

		// =====================================================
		// PHASE 2 → BULK FETCH UNITS (1 QUERY 🔥)
		// =====================================================

		Map<Long, UnitMasterEntity> unitCache = unitMasterRepository
				.findAllByIdInAndIsDeleteFalseAndIsActiveTrue(unitIds).stream()
				.collect(Collectors.toMap(UnitMasterEntity::getId, u -> u));

		// =====================================================
		// PHASE 3 → CONVERT ONCE
		// =====================================================

		for (AgencyRawMaterialReportResponseDto agency : agencyMap.values()) {

			for (AgencyRawMaterialCategoryResponseDto cat : agency.getRawMaterials()) {

				for (AgencyRawMaterialItemsResponseDto item : cat.getRawItems()) {

					Map<Long, Double> converted = convertToParentUnit(item.getUnitQtyMap());

					Long finalUnit = item.getUnitId();
					double finalQty = item.getUnitQtyMap().values().stream().mapToDouble(Double::doubleValue).sum();

					if (!converted.isEmpty()) {
						Map.Entry<Long, Double> e = converted.entrySet().iterator().next();

						finalUnit = e.getKey();
						finalQty = e.getValue();
					}

					UnitMasterEntity unit = unitCache.get(finalUnit);

					BigDecimal finalWeight = BigDecimal.ZERO;

					if (finalQty > 0) {
						if (item.getIsApplyCal()) {

							finalWeight = roundOffUtility.applyUnitRange(BigDecimal.valueOf(finalQty), unit, null);
						} else {
							finalWeight = BigDecimal.valueOf(finalQty);
						}
					} else {
						finalWeight = BigDecimal.valueOf(finalQty);
					}

					UnitConversionResult result = unitConversionService.autoConvert(unit, finalWeight);

					item.setUnitId(result.getUnit().getId());
					item.setTotalWeight(result.getQuantity().doubleValue());

					String unitName = lang == 1 ? result.getUnit().getNameHindi()
							: lang == 2 ? result.getUnit().getNameGujarati() : result.getUnit().getNameEnglish();

					item.setUnitName(unitName);
				}
			}
		}

		return new ArrayList<>(agencyMap.values());
	}

	@Override
	public List<EventRawMaterialPartyDTO> getGroupedPartyByEventAndFunction(Long eventId, Long eventFunctionId) {

		return eventRawMaterialRepository.findGroupedPartyByEventAndFunction(eventId, eventFunctionId);
	}

	@Override
	public Map<String, Map<String, List<SupplierRawMaterialResponseDto>>> getSupplierwiseRawMaterial(Long eventId,
			int lang, List<Long> agencyId) {

		List<SupplierRawMaterialResponseDto> responseDtos = new ArrayList<>();

		List<Object[]> result = eventRawMaterialRepository.getSupplierwiseRawMaterial(eventId, lang, agencyId);

		for (Object[] row : result) {
			SupplierRawMaterialResponseDto dto = new SupplierRawMaterialResponseDto();
			int index = 0;

			dto.setSupplireId(commonService.getLong(row[index++]));
			dto.setSupplierName(commonService.getString(row[index++]));
			dto.setRawMaterialCatId(commonService.getLong(row[index++]));
			dto.setRawMaterialCatName(commonService.getString(row[index++]));
			dto.setRawMaterialId(commonService.getLong(row[index++]));
			dto.setRawMaterialName(commonService.getString(row[index++]));
			dto.setQty(commonService.getBigDecimal(row[index++]));
			dto.setUnit(commonService.getString(row[index++]));
			dto.setDelieveryPlace(commonService.getString(row[index++]));

			Object value = row[index++];

			if (value != null) {
				Timestamp ts = (Timestamp) value;
				LocalDateTime ldt = ts.toLocalDateTime();

				dto.setDelieveryDateTime(ldt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")));
			} else {
				dto.setDelieveryDateTime(null);
			}

			responseDtos.add(dto);
		}

		return responseDtos.stream()
				.collect(Collectors.groupingBy(dto -> dto.getSupplierName() != null ? dto.getSupplierName() : "NULL",
						LinkedHashMap::new,
						Collectors.groupingBy(
								dto -> dto.getRawMaterialCatName() != null ? dto.getRawMaterialCatName() : "NULL",
								LinkedHashMap::new, Collectors.toList())));
	}

	@Override
	public List<EventRawMaterialPartyDTO> getGroupedPartyByEvent(Long eventId) {

		return eventRawMaterialRepository.findGroupedPartyByEvent(eventId);
	}

	public void defaultSave(Long eventId, Long rawMaterialCategoryId, List<EventRawMaterialResponse> responseList) {
		if (responseList.size() != 0) {
			EventRawMaterialRequest request = convertToRequest(eventId, rawMaterialCategoryId, responseList);
			addOrUpdateEventRawMaterial(request);
		}
	}

	public EventRawMaterialRequest convertToRequest(Long eventId, Long rawMaterialCategoryId,
			List<EventRawMaterialResponse> responseList) {

		EventRawMaterialRequest request = new EventRawMaterialRequest();
		request.setEventId(eventId);
		request.setRawMaterialCategoryId(rawMaterialCategoryId);

		List<EventRawMaterialDetailRequest> detailList = new ArrayList<>();

		for (EventRawMaterialResponse res : responseList) {
			EventRawMaterialDetailRequest detail = new EventRawMaterialDetailRequest();
			detail.setSupplierId(res.getSupplierId() == null || res.getSupplierId() == 0 ? 0L : res.getSupplierId());

			detail.setRawMaterialId(res.getRawMaterialId());
			detail.setRawMaterialCatId(res.getRawMaterialCatId());
			detail.setUnitId(res.getUnitId());

			detail.setQty(res.getIsApplyCal() ? roundOffUtility
					.applyUnitRange(res.getQty() == null ? BigDecimal.ZERO : BigDecimal.valueOf(res.getQty()),
							res.getUnitId() == null ? null
									: unitMasterRepository.findByIdAndIsDeleteFalse(res.getUnitId()).get(),
							null)
					.doubleValue() : (res.getQty() == null ? 0.0 : res.getQty()));

			detail.setFinalQty(res.getIsApplyCal() ? roundOffUtility
					.applyUnitRange(res.getFinalQty() == null ? BigDecimal.ZERO : BigDecimal.valueOf(res.getFinalQty()),
							res.getUnitId() == null ? null
									: unitMasterRepository.findByIdAndIsDeleteFalse(res.getUnitId()).get(),
							null)
					.doubleValue() : (res.getFinalQty() == null ? 0.0 : res.getFinalQty()));
			detail.setPlace(res.getPlace());
			detail.setTotalprice(res.getTotalprice() == null ? 0.0 : res.getTotalprice());
			// ==========================================
			// Convert Nested Function Details
			// ==========================================
			List<EventRawMaterialFunctionsDetailRequest> functionDetailList = new ArrayList<>();

			if (res.getEventRawMaterialFunctions() != null) {
				for (EventRawMaterialFunctionsDto f : res.getEventRawMaterialFunctions()) {

					EventRawMaterialFunctionsDetailRequest fd = new EventRawMaterialFunctionsDetailRequest();

					fd.setFunctionId(f.getFunctionId());
					fd.setMenuItemId(f.getMenuItemId());

					fd.setEventFunctionId(f.getEventFunctionId());
					fd.setQty(
							res.getIsApplyCal()
									? roundOffUtility.applyUnitRange(
											f.getQty() == null ? BigDecimal.ZERO : BigDecimal.valueOf(f.getQty()),
											f.getUnitId() == null ? null
													: unitMasterRepository.findByIdAndIsDeleteFalse(f.getUnitId())
															.get(),
											null).doubleValue()
									: (f.getQty() == null ? 0.0 : f.getQty()));
					fd.setItemName(f.getItemName());

					// Supplier Id
					fd.setSupplierId(f.getSupplierId() == null || f.getSupplierId() == 0 ? 0L : f.getSupplierId());

					// Unit Id
					fd.setUnitId(f.getUnitId());

					// Place
					fd.setPlace(f.getPlace());

					// Price
					fd.setPrice(f.getPrice() == null ? 0.0 : f.getPrice());

					// Function date & extra field flag
					fd.setFunctiondatetime(f.getFunctiondatetime());
					fd.setIsExtraField(f.getIsExtraField());
					fd.setRawMaterialRate(f.getRawMaterialPrice());
					functionDetailList.add(fd);
				}
			}

			detail.setEventRawMatFunctions(functionDetailList);
			detailList.add(detail);
		}

		request.setEventRawMaterial(detailList);
		return request;
	}

	@Override
	public boolean addEventRawMaterialForApp(EventRawMaterialAppRequest request) {
		try {
			EventMasterEntity e = eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId()).orElseThrow(
					() -> new RuntimeException("Event Master with this eventId not found: " + request.getEventId()));
			List<EventRawMaterialEntity> erfun = new ArrayList<>();
			for (EventRawMaterialAppRequestDetails ef : request.getEventRawMaterial()) {
				Optional<EventRawMaterialEntity> entity = eventRawMaterialRepository
						.findByEvent_IdAndRawMaterial_IdAndIsDeleteFalse(request.getEventId(), ef.getRawMaterialId());
				if (entity.isPresent()) {
					EventRawMaterialEntity er = entity.get();
					er.setEvent(e);
					er.setSupplier(ef.getSupplierId() != 0 ? partyMasterRepository.getById(ef.getSupplierId()) : null);
					er.setUnit(unitMasterRepository.getById(ef.getUnitId()));
					er.setQty(ef.getQty());
					er.setFinalqty(ef.getFinalQty());
					er.setPlace(ef.getPlace());
					er.setTotalprice(ef.getTotalprice());
					erfun.add(er);
				}
			}
			if (erfun.size() != 0) {
				eventRawMaterialRepository.saveAll(erfun);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to add/update event raw material functions", e);
		}
	}

	@Override
	public List<GeneralFixResponseDto> getGeneralFixItems(Long eventId, Long eventFunctionId,
			List<Long> rawMaterialCatIds, int lang, Integer pax, Long userId) {

		int applyFilter = (rawMaterialCatIds == null || rawMaterialCatIds.isEmpty()) ? 0 : 1;

//		List<Object[]> result = eventRawMaterialRepository.getGeneralFixItems(eventId, eventFunctionId,
//				applyFilter == 1 ? rawMaterialCatIds : Collections.singletonList(0L), lang, applyFilter);

		List<Object[]> result = eventRawMaterialRepository.getGeneralFixRawItems(applyFilter, lang,
				applyFilter == 1 ? rawMaterialCatIds : Collections.singletonList(0L), userId);
		System.out.println("Data Size:-" + result.size());

		Map<Long, GeneralFixResponseDto> dtoMap = new LinkedHashMap<>();

		for (Object[] row : result) {
			Long rawMaterialId = commonService.getLong(row[0]);
			Long rawMaterialCatId = commonService.getLong(row[1]);
			String rawMaterialCatName = commonService.getString(row[2]);
			String rawMaterialName = commonService.getString(row[3]);
			BigDecimal weight = commonService.getBigDecimal(row[4]);
			Long unitId = commonService.getLong(row[5]);
			Optional<UnitMasterEntity> unitOp = unitMasterRepository.findByIdAndIsDeleteFalse(unitId);

			RawMaterialMasterEntity rawMaterialMasterEntity = rawMaterialMasterRepository
					.findByIdAndIsDeleteFalse(rawMaterialId);
			BigDecimal quantity = calculateDefaultWeight(weight, pax);
			if (rawMaterialMasterEntity.getIsApplyCal()) {
				quantity = roundOffUtility.applyUnitRange(quantity, unitOp.orElse(null), null);
			}
			BigDecimal totalWeight;
			UnitMasterEntity finalUnit;

			if (unitOp.isPresent() && unitOp.get().getParentUnit() != null && unitOp.get().getEquivalentValue() != null
					&& unitOp.get().getEquivalentValue() != 0.0) {

				UnitMasterEntity childUnit = unitOp.get();
				UnitMasterEntity parentUnit = childUnit.getParentUnit();

				Double convertedVal = convertUnitQtyToParentQty(childUnit, quantity.doubleValue());
				totalWeight = BigDecimal.valueOf(convertedVal);
				finalUnit = parentUnit;

			} else {
				totalWeight = quantity;
				finalUnit = unitOp.orElse(null);
			}
			if (dtoMap.containsKey(rawMaterialId)) {

				GeneralFixResponseDto existingDto = dtoMap.get(rawMaterialId);

				existingDto.setWeight(existingDto.getWeight().add(totalWeight));

			} else {

				GeneralFixResponseDto dto = new GeneralFixResponseDto();

				dto.setRawMaterialId(rawMaterialId);
				dto.setRawMaterialCatId(rawMaterialCatId);
				dto.setRawMaterialCatName(rawMaterialCatName);
				dto.setRawMaterialName(rawMaterialName);
				dto.setWeight(totalWeight);

				if (finalUnit != null) {
					dto.setUnitId(finalUnit.getId());
					dto.setUnit(finalUnit);
					dto.setUnitName(finalUnit.getNameEnglish());
				}

				dtoMap.put(rawMaterialId, dto);
			}
		}

		return new ArrayList<>(dtoMap.values());
	}

	private BigDecimal calculateDefaultWeight(BigDecimal weight, Integer newPax) {
		return safeMultiply(bd(newPax), weight).divide(HUNDRED, 2, RoundingMode.HALF_UP);
	}

	private BigDecimal bd(Integer pax) {
		return pax == null ? ZERO : BigDecimal.valueOf(pax);
	}

	@Override
	public List<GeneralFixResponseDto> getFunctions(Long eventId, Long eventFunctionId, int lang) {

		List<GeneralFixResponseDto> dtos = new ArrayList<>();

		List<Object[]> result = eventRawMaterialRepository.getFunctions(eventId, eventFunctionId, lang);

		for (Object[] row : result) {
			int index = 0;
			GeneralFixResponseDto dto = new GeneralFixResponseDto();

			dto.setEventFunctionId(commonService.getLong(row[index++]));
			dto.setFunctionName(commonService.getString(row[index++]));
			dto.setEventNo(commonService.getString(row[index++]));
			dto.setFunctionVenue(commonService.getString(row[index++]));
			dto.setPerson(commonService.getInteger(row[index++]));

			dtos.add(dto);
		}

		return dtos;
	}

	@Override
	public List<GeneralFixResponseDto> getFunctions(Long eventId, List<Long> eventFunctionIds, int lang) {

		int flag = -1;
		if (eventFunctionIds != null && !eventFunctionIds.isEmpty() && eventFunctionIds.get(0) != -1)
			flag = 1;
		else
			eventFunctionIds = Collections.singletonList(-1L);

		List<GeneralFixResponseDto> dtos = new ArrayList<>();

		List<Object[]> result = eventRawMaterialRepository.getFunctions(eventId, eventFunctionIds, lang, flag);
		for (Object[] row : result) {
			int index = 0;
			GeneralFixResponseDto dto = new GeneralFixResponseDto();

			dto.setEventFunctionId(commonService.getLong(row[index++]));
			dto.setFunctionName(commonService.getString(row[index++]));
			dto.setEventNo(commonService.getString(row[index++]));
			dto.setFunctionVenue(commonService.getString(row[index++]));
			dto.setPerson(commonService.getInteger(row[index++]));

			dtos.add(dto);
		}

		return dtos;
	}

	@Override
	public List<GeneralFixResponseDto> getCrockerCategory(int lang) {

		List<GeneralFixResponseDto> dtos = new ArrayList<>();

		List<Object[]> result = eventRawMaterialRepository.getCrockerCategories(lang);

		for (Object[] row : result) {
			int index = 0;

			GeneralFixResponseDto dto = new GeneralFixResponseDto();
			dto.setRawMaterialCatId(commonService.getLong(row[index++]));
			dto.setRawMaterialCatName(commonService.getString(row[index++]));

			dtos.add(dto);
		}

		return dtos;
	}

	@Override
	public List<GeneralFixResponseDto> getCrockerItems(Long rawMaterialCatId, int lang, Integer person) {
		List<GeneralFixResponseDto> dtos = new ArrayList<>();

		List<Object[]> result = eventRawMaterialRepository.getCrockerItems(rawMaterialCatId, lang, person);

		for (Object[] row : result) {
			int index = 0;

			GeneralFixResponseDto dto = new GeneralFixResponseDto();
			dto.setRawMaterialId(commonService.getLong(row[index++]));
			dto.setRawMaterialName(commonService.getString(row[index++]));
			dto.setFinalQty(commonService.getDouble(row[index++]));

			dtos.add(dto);
		}

		return dtos;
	}

	@Override
	public Map<String, Map<String, BigDecimal>> getAllCrockerCutlery(int lang) {
		Map<String, Map<String, BigDecimal>> data = new LinkedHashMap<>();

		List<Object[]> result = eventRawMaterialRepository.getAllCrockerCutleryData(lang);

		for (Object[] row : result) {
			String catName = (String) row[0];
			String itemName = (String) row[1];
			BigDecimal qty = BigDecimal.ZERO;

			data.putIfAbsent(catName, new LinkedHashMap<>());
			Map<String, BigDecimal> itemMap = data.get(catName);

			itemMap.merge(itemName, qty, BigDecimal::add);
		}

		return data;
	}

	@Override
	public Map<String, Map<String, BigDecimal>> getFunctionwiseCrockerCutlery(int lang, Integer person,
			Map<String, Map<String, BigDecimal>> crockerCutleryData, Long userId) {
		List<Object[]> result = eventRawMaterialRepository.getAllCrockerCutleryData(lang, person, userId);

		for (Object[] row : result) {
			String catName = (String) row[0];
			String itemName = (String) row[1];
			BigDecimal qty = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;

			crockerCutleryData.putIfAbsent(catName, new LinkedHashMap<>());
			Map<String, BigDecimal> itemMap = crockerCutleryData.get(catName);

			itemMap.merge(itemName, qty, BigDecimal::add);
		}

		return crockerCutleryData;
	}

	public List<EventRawMaterialCategoryResponse> getDateWiseRawMaterial(List<Long> rawMaterialCatIds,
			LocalDateTime startDate, LocalDateTime endDate, Long userId) {

		Boolean flag = !(rawMaterialCatIds == null || rawMaterialCatIds.isEmpty());

		List<EventRawMaterialEntity> entities = eventRawMaterialFunctionsRepository
				.dateWiseReportData(rawMaterialCatIds, startDate, endDate, flag, userId);

		List<ExtraEventRawMaterialEntity> extraRaw = eventExtraRawMaterialRepository.dateWiseExtraRaw(rawMaterialCatIds,
				startDate, endDate, flag, userId);

		Map<Long, EventRawMaterialCategoryResponse> categoryMap = new LinkedHashMap<>();

		// NORMAL RAW MATERIAL
		for (EventRawMaterialEntity e : entities) {

			Long categoryId = e.getRawMaterialCat().getId();

			EventRawMaterialCategoryResponse categoryResponse = categoryMap.computeIfAbsent(categoryId,
					id -> new EventRawMaterialCategoryResponse(id, e.getRawMaterialCat().getNameEnglish(),
							e.getRawMaterialCat().getNameHindi(), e.getRawMaterialCat().getNameGujarati(),
							new ArrayList<>()));

			categoryResponse.getRawMaterials().add(new EventRawMaterialInfoDto(e.getId(), e.getRawMaterial().getId(),
					e.getRawMaterial().getNameEnglish(), e.getRawMaterial().getNameHindi(),
					e.getRawMaterial().getNameGujarati(), e.getSupplier() != null ? e.getSupplier().getId() : 0L,
					e.getSupplier() != null ? e.getSupplier().getNameEnglish() : "",
					e.getUnit() != null ? e.getUnit().getId() : null,
					e.getUnit() != null ? e.getUnit().getSymbolEnglish() : "", roundToTwoDecimalDynamic(e.getQty()),
					roundToTwoDecimalDynamic(e.getFinalqty()), e.getPlace(),
					roundToTwoDecimalDynamic(e.getTotalprice()), e.getRawMaterial().getSequence()));
		}

		// EXTRA RAW MATERIAL
		for (ExtraEventRawMaterialEntity e : extraRaw) {

			Long categoryId = e.getRawmaterialCatId();

			RawMaterialCategoryMasterEntity category = rawMaterialCategoryMasterRepository.findById(categoryId)
					.orElse(null);

			if (category == null) {
				continue;
			}

			EventRawMaterialCategoryResponse categoryResponse = categoryMap.computeIfAbsent(categoryId,
					id -> new EventRawMaterialCategoryResponse(id, category.getNameEnglish(), category.getNameHindi(),
							category.getNameGujarati(), new ArrayList<>()));

			UnitMasterEntity unit = null;
			if (e.getUnitId() != null) {
				unit = unitMasterRepository.findById(e.getUnitId()).orElse(null);
			}

			PartyMasterEntity supplier = null;
			if (e.getPartyId() != null) {
				supplier = partyMasterRepository.findById(e.getPartyId()).orElse(null);
			}

			categoryResponse.getRawMaterials()
					.add(new EventRawMaterialInfoDto(e.getId(), 0L, e.getExtraRawmaterial(), "", "",
							supplier != null ? supplier.getId() : 0L, supplier != null ? supplier.getNameEnglish() : "",
							unit != null ? unit.getId() : null, unit != null ? unit.getSymbolEnglish() : "",
							roundToTwoDecimalDynamic(e.getQty()), roundToTwoDecimalDynamic(e.getFinalqty()),
							e.getPlace(), roundToTwoDecimalDynamic(e.getTotalprice()), 9999));
		}

		categoryMap.values()
				.forEach(c -> c.setRawMaterials(filterByRawMaterialId(c.getRawMaterials().stream()
						.sorted(Comparator.comparing(d -> d.getSequence() == null ? 9999 : d.getSequence()))
						.collect(Collectors.toList()))));

		return new ArrayList<>(categoryMap.values());
	}

}