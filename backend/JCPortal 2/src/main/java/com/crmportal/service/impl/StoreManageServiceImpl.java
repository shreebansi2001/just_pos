package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.*;
import com.crmportal.repository.*;
import com.crmportal.request.dto.*;
import com.crmportal.response.dto.*;
import com.crmportal.service.StoreManageService;
import com.crmportal.service.StockLedgerEntryService;

@Service
@Transactional
public class StoreManageServiceImpl implements StoreManageService {

	@Autowired
	private StoreManageRepository storeManageRepository;

	@Autowired
	private StoreManageDetailRepository detailRepository;

	@Autowired
	private RawMaterialMasterRepository rawMaterialRepository;

	@Autowired
	private RawMaterialCategoryMasterRepository rawMaterialCatRepository;

	@Autowired
	private UnitMasterRepository unitMasterRepository;

	@Autowired
	private UserMasterRepository userRepository;

	@Autowired
	private StockLedgerRepository stockLedgerRepository;

	@Autowired
	private StockLedgerEntryService stockLedgerEntryService;
	
	@Autowired
	private StockTypeRepository stockTypeRepository;

	private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	// ── Load today's items grouped by category ────────────────────────────
	@Override
	public Page<StoreManageCategoryDto> loadTodayItems(Long userId, Pageable pageable, Long categoryId,
			String itemName, Long stockTypeId) {

		String name = itemName != null && !itemName.trim().isEmpty() ? itemName : "";
		Page<RawMaterialMasterEntity> rawMaterialPage = rawMaterialRepository
				.findByRawMaterialCatIdAndUserIdAndIsDeleteFalseAndNameEnglishContainingIgnoreCase(categoryId, userId,
						name, pageable);

		Map<String, StoreManageCategoryDto> catMap = new LinkedHashMap<>();

		for (RawMaterialMasterEntity rm : rawMaterialPage.getContent()) {

			String catName = rm.getRawMaterialCat() != null ? rm.getRawMaterialCat().getNameEnglish() : "OTHERS";

			Long catId = rm.getRawMaterialCat() != null ? rm.getRawMaterialCat().getId() : -1L;

			catMap.putIfAbsent(catName, buildCatDto(catId, catName));

			StoreManageItemResponseDto itemDto = new StoreManageItemResponseDto();

			itemDto.setRawMaterialId(rm.getId());
			itemDto.setRawMaterialName(rm.getNameEnglish());
			itemDto.setCatId(catId);
			itemDto.setCatName(catName);

			if (rm.getUnit() != null) {
				itemDto.setUnitId(rm.getUnit().getId());
				itemDto.setUnitName(rm.getUnit().getNameEnglish());
			}

			double closing = calculateClosingStock(rm, stockTypeId);

			itemDto.setClosingStock(closing);
			itemDto.setStoreQty(closing);
			itemDto.setIncreaseQty(0.0);
			itemDto.setWastageQty(0.0);

			catMap.get(catName).getItems().add(itemDto);
		}

		List<StoreManageCategoryDto> categories = new ArrayList<>(catMap.values());

		return new PageImpl<>(categories, pageable, rawMaterialPage.getTotalElements());
	}

	// ── Save store manage ─────────────────────────────────────────────────
	@Override
	public StoreManageResponseDto save(StoreManageRequestDto request) {

		UserMasterEntity user = userRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

		LocalDate manageDate = request.getManageDate() != null && !request.getManageDate().isEmpty()
				? LocalDate.parse(request.getManageDate(), FMT)
				: LocalDate.now();

		// ── Find existing or create new ───────────────────────────────────
		/*StoreManageEntity entity = storeManageRepository.findByUserIdAndDate(request.getUserId(), manageDate)
				.orElse(null);
		
		StockTypeEntity stockType = null;

		if (request.getStockTypeId() != null) {
		    stockType = stockTypeRepository.findById(request.getStockTypeId())
		            .orElseThrow(() -> new RuntimeException("Stock Type not found"));
		}*/
		
		StockTypeEntity stockType = null;

		if (request.getStockTypeId() != null) {
		    stockType = stockTypeRepository.findById(request.getStockTypeId())
		            .orElseThrow(() -> new RuntimeException("Stock Type not found"));
		}

		StoreManageEntity entity;

		if (request.getStockTypeId() != null) {

		    entity = storeManageRepository
		            .findByUser_IdAndManageDateAndStockType_Id(
		                    request.getUserId(),
		                    manageDate,
		                    request.getStockTypeId()
		            )
		            .orElse(null);

		} else {

		    entity = storeManageRepository
		            .findByUser_IdAndManageDateAndStockTypeIsNull(
		                    request.getUserId(),
		                    manageDate
		            )
		            .orElse(null);
		}

		if (entity == null) {
			entity = new StoreManageEntity();
			entity.setVoucherNo(generateVoucherNo(request.getUserId()));
			entity.setManageDate(manageDate);
			entity.setUser(user);
			entity.setIsDelete(false);
			entity.setStockType(stockType); // null pan hoi sake
			entity = storeManageRepository.save(entity);
		}

		final StoreManageEntity savedEntity = entity;

		// ── Save details — only items where storeQty > 0 ─────────────────
		List<StoreManageDetailEntity> details = new ArrayList<>();

		if (request.getItems() != null) {
			for (StoreManageItemRequestDto item : request.getItems()) {

				if (item.getStoreQty() == null)
					continue;

				RawMaterialMasterEntity rm = rawMaterialRepository.findById(item.getRawMaterialId()).orElse(null);
				if (rm == null) {
					continue;
				}

				StoreManageDetailEntity detail = new StoreManageDetailEntity();
				detail.setStoreManage(savedEntity);
				detail.setRawMaterial(rm);
				detail.setRawMaterialCat(rm.getRawMaterialCat());
				detail.setUnit(rm.getUnit());
				detail.setClosingStock(item.getClosingStock() != null ? item.getClosingStock() : 0.0);
				detail.setStoreQty(item.getStoreQty());
				detail.setIncreaseQty(item.getIncreaseQty() != null ? item.getIncreaseQty() : 0.0);
				detail.setWastageQty(item.getWastageQty() != null ? item.getWastageQty() : 0.0);
				detail.setUserId(request.getUserId());
				detail.setRemarks(item.getRemarks());
				details.add(detail);
			}
		}

		detailRepository.saveAll(details);
		detailRepository.flush();

		// ── Stock Ledger entries ──────────────────────────────────────────
		for (StoreManageDetailEntity d : details) {

			// Increase Qty → stock IN
			if (d.getIncreaseQty() != null && d.getIncreaseQty() > 0) {
				stockLedgerEntryService.saveStoreManageIncrease(d.getRawMaterial(), d.getIncreaseQty(), savedEntity,
						d.getUserId(),d.getRawMaterialCat(),d.getUnit());
			}

			// Wastage Qty → stock OUT
			if (d.getWastageQty() != null && d.getWastageQty() > 0) {
				stockLedgerEntryService.saveStoreManageWastage(d.getRawMaterial(), d.getWastageQty(), savedEntity,
						d.getUserId(),d.getRawMaterialCat(),d.getUnit());
			}
		}

		return toResponse(savedEntity);
	}

	// ── Get all ───────────────────────────────────────────────────────────
	@Override
	public List<StoreManageResponseDto> getAll(Long userId) {
		return storeManageRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse)
				.collect(Collectors.toList());
	}

	// ── Get by ID ─────────────────────────────────────────────────────────
	@Override
	public StoreManageResponseDto getById(Long id) {
		StoreManageEntity entity = storeManageRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Store Manage not found with id: " + id));
		return toResponseWithDetails(entity);
	}

	// ── Helpers ───────────────────────────────────────────────────────────

	private StoreManageCategoryDto buildCatDto(Long catId, String catName) {
		StoreManageCategoryDto dto = new StoreManageCategoryDto();
		dto.setCatId(catId);
		dto.setCatName(catName);
		dto.setItems(new ArrayList<>());
		return dto;
	}

	private StoreManageResponseDto toResponse(StoreManageEntity entity) {
		StoreManageResponseDto dto = new StoreManageResponseDto();
		dto.setId(entity.getId());
		dto.setVoucherNo(entity.getVoucherNo());
		dto.setManageDate(entity.getManageDate() != null ? entity.getManageDate().format(FMT) : null);
		dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
		dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
		if (entity.getStockType() != null) {
	        dto.setStockTypeId(entity.getStockType().getId());
	        dto.setStockTypeName(entity.getStockType().getNameEnglish());
	    }
		return dto;
	}

	private StoreManageResponseDto toResponseWithDetails(StoreManageEntity entity) {

		StoreManageResponseDto dto = toResponse(entity);

		List<StoreManageDetailEntity> details = detailRepository.findByStoreManageId(entity.getId());

		Map<String, StoreManageCategoryDto> catMap = new LinkedHashMap<>();

		for (StoreManageDetailEntity d : details) {

			String catName = d.getRawMaterialCat() != null ? d.getRawMaterialCat().getNameEnglish() : "OTHERS";
			Long catId = d.getRawMaterialCat() != null ? d.getRawMaterialCat().getId() : -1L;

			catMap.putIfAbsent(catName, buildCatDto(catId, catName));

			StoreManageItemResponseDto itemDto = new StoreManageItemResponseDto();
			itemDto.setRawMaterialId(d.getRawMaterial().getId());
			itemDto.setRawMaterialName(d.getRawMaterial().getNameEnglish());
			itemDto.setCatId(catId);
			itemDto.setCatName(catName);

			if (d.getUnit() != null) {
				itemDto.setUnitId(d.getUnit().getId());
				itemDto.setUnitName(d.getUnit().getNameEnglish());
			}

			itemDto.setClosingStock(d.getClosingStock());
			itemDto.setStoreQty(d.getStoreQty());
			itemDto.setIncreaseQty(d.getIncreaseQty());
			itemDto.setWastageQty(d.getWastageQty());
			itemDto.setRemarks(d.getRemarks());
			
			catMap.get(catName).getItems().add(itemDto);
		}

		dto.setCategories(new ArrayList<>(catMap.values()));
		return dto;
	}

	private String generateVoucherNo(Long userId) {
		long count = storeManageRepository.countByUserId(userId);
		return "SM" + String.format("%05d", count + 1);
	}

	private Double calculateClosingStock(RawMaterialMasterEntity rm,Long stockTypeId) {
		try {
			LocalDate from = LocalDate.of(2000, 1, 1);
			LocalDate to = LocalDate.now();
			Long rmId = rm.getId();

			boolean hasParentUnit = false;
			UnitMasterEntity unitForConv = null;

			if (rm.getUnit() != null) {
				Optional<UnitMasterEntity> rmUnitOp = unitMasterRepository
						.findByIdAndIsParentUnitFalse(rm.getUnit().getId());
				if (rmUnitOp.isPresent() && rmUnitOp.get().getParentUnit() != null
						&& rmUnitOp.get().getEquivalentValue() != null && rmUnitOp.get().getEquivalentValue() != 0.0) {
					hasParentUnit = true;
					unitForConv = rmUnitOp.get();
				}
			}

			double opb = rm.getOpbStock() != null ? rm.getOpbStock().doubleValue() : 0.0;
			if (hasParentUnit)
				opb = convert(unitForConv, opb);

			double purchase = (stockTypeId == null)
			        ? stockLedgerRepository.sumQtyInByRawMaterialAndDateRangeAndRefType(
			                rmId, from, to, "PURCHASE")
			        : stockLedgerRepository.sumQtyInByRawMaterialAndDateRangeAndRefTypeAndStockType(
			                rmId, from, to, stockTypeId, "PURCHASE");
			if (hasParentUnit)
				purchase = convert(unitForConv, purchase);

			double purchaseReturn = (stockTypeId == null)
			        ? stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefType(
			                rmId, from, to, "PURCHASE_RETURN")
			        : stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefTypeAndStockType(
			                rmId, from, to, stockTypeId, "PURCHASE_RETURN");
			if (hasParentUnit)
				purchaseReturn = convert(unitForConv, purchaseReturn);

			double sell =
			        (stockTypeId == null
			                ? stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to, "STORE_ISSUE")
			                : stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefTypeAndStockType(rmId, from, to, stockTypeId, "STORE_ISSUE"))
			        +
			        (stockTypeId == null
			                ? stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to, "CHEF_REQUISITION")
			                : stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefTypeAndStockType(rmId, from, to, stockTypeId, "CHEF_REQUISITION"))
			        +
			        (stockTypeId == null
			                ? stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to, "SOT_STORE_ISSUE")
			                : stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefTypeAndStockType(rmId, from, to, stockTypeId, "SOT_STORE_ISSUE"));
			
			if (hasParentUnit)
				sell = convert(unitForConv, sell);

			double sellReturn = (stockTypeId == null ? stockLedgerRepository.sumQtyInByRawMaterialAndDateRangeAndRefType(rmId, from, to,
					"STORE_ISSUE_RETURN") : stockLedgerRepository.sumQtyInByRawMaterialAndDateRangeAndRefTypeAndStockType(rmId, from, to, stockTypeId,
							"STORE_ISSUE_RETURN"))
					+ 
					(stockTypeId == null ?
					stockLedgerRepository.sumQtyInByRawMaterialAndDateRangeAndRefType(rmId, from, to,
							"SOT_STORE_RETURN") : stockLedgerRepository.sumQtyInByRawMaterialAndDateRangeAndRefTypeAndStockType(rmId, from, to, stockTypeId,
									"SOT_STORE_RETURN")) ;
			if (hasParentUnit)
				sellReturn = convert(unitForConv, sellReturn);

			// Also include previous store manage adjustments
			double smIncrease = (stockTypeId == null ? stockLedgerRepository.sumQtyInByRawMaterialAndDateRangeAndRefType(rmId, from, to,
					"INCREASE") : stockLedgerRepository.sumQtyInByRawMaterialAndDateRangeAndRefTypeAndStockType(rmId, from, to, stockTypeId,
							"INCREASE")) ;
			if (hasParentUnit)
				smIncrease = convert(unitForConv, smIncrease);

			double smWastage = (stockTypeId == null ? stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to,
					"WASTAGE") : stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefTypeAndStockType(rmId, from, to, stockTypeId,
							"WASTAGE"));
			if (hasParentUnit)
				smWastage = convert(unitForConv, smWastage);

			double closing = opb + purchase - purchaseReturn - sell + sellReturn + smIncrease - smWastage;

			return BigDecimal.valueOf(closing).setScale(2, RoundingMode.HALF_UP).doubleValue();

		} catch (Exception e) {
			return 0.0;
		}
	}

	private double convert(UnitMasterEntity unit, double qty) {
		if (qty == 0.0)
			return 0.0;
		return BigDecimal.valueOf(qty).divide(BigDecimal.valueOf(unit.getEquivalentValue()), 2, RoundingMode.HALF_UP)
				.doubleValue();
	}
}