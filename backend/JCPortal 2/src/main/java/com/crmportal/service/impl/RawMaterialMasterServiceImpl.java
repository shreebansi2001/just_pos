package com.crmportal.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

import com.crmportal.entity.ContactCategoryMasterEntity;
import com.crmportal.entity.CrockeryCutleryEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.PurchaseRequestDetailsEntity;
import com.crmportal.entity.RawMaterialCategoryMasterEntity;
import com.crmportal.entity.RawMaterialCategoryTypeMasterEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.RawMaterialSupplierEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.mapper.PartyMasterMapper;
import com.crmportal.mapper.RawMaterialCategoryMasterMapper;
import com.crmportal.mapper.RawMaterialMasterMapper;
import com.crmportal.mapper.RawMaterialSupplierMapper;
import com.crmportal.mapper.UserMasterMapper;
import com.crmportal.repository.CrockeryCutleryRepository;
import com.crmportal.repository.MenuItemRawMaterialRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.PurchaseRequestDetailsRepository;
import com.crmportal.repository.RawMaterialCategoryMasterRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.RawMaterialSupplierRepository;
import com.crmportal.repository.StockLedgerRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.RawMaterialMasterRequestDto;
import com.crmportal.request.dto.RawMaterialSupplierRequestDto;
import com.crmportal.request.dto.UpdateRawMaterialRequestDto;
import com.crmportal.response.dto.PartyMasterResponseDto;
import com.crmportal.response.dto.RawMaterialCategoryChangeResponseDto;
import com.crmportal.response.dto.RawMaterialCategoryMasterResponseDto;
import com.crmportal.response.dto.RawMaterialInMenuItemResponseDto;
import com.crmportal.response.dto.RawMaterialMasterResponseDto;
import com.crmportal.response.dto.RawMaterialSupplierResponseDto;
import com.crmportal.response.dto.UnitHierarchyDto;
import com.crmportal.response.dto.UserMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.DatabasePlanningService;
import com.crmportal.service.RawMaterialMasterService;
import com.crmportal.service.UnitMasterService;
import com.crmportal.service.UserFileService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RawMaterialMasterServiceImpl implements RawMaterialMasterService {

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	RawMaterialCategoryMasterRepository rawMaterialCatMasterRepository;

	@Autowired
	RawMaterialMasterRepository rawMaterialMasterRepository;

	@Autowired
	RawMaterialMasterMapper rawMaterialMasterMapper;

	@Autowired
	CommonService commonService;

	@Autowired
	RawMaterialSupplierMapper rawMaterialSupplierMapper;

	@Autowired
	RawMaterialSupplierRepository rawMaterialSupplierRepository;

	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Autowired
	RawMaterialCategoryMasterRepository rawMaterialCategoryMasterRepository;

	@Autowired
	PartyMasterMapper partyMasterMapper;

	@Autowired
	UserMasterMapper userMasterMapper;

	@Autowired
	UnitMasterRepository unitMasterRepository;

	@Autowired
	UnitMasterService unitMasterService;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Autowired
	DatabasePlanningService databasePlanningService;

	@Autowired
	RawMaterialCategoryMasterMapper rawMaterialCategoryMasterMapper;

	@Autowired
	CrockeryCutleryRepository crockeryCutleryRepository;

	@Autowired
	UserFileService userFileService;

	@Autowired
	MenuItemRawMaterialRepository itemRawMaterialRepository;
	
	@Autowired
	private StockLedgerRepository stockLedgerRepository;

	@Autowired
	PurchaseRequestDetailsRepository purchaseRequestDetailsRepository;
	
	@Autowired
	private Environment environment;

	private static final int CROCKERY_TYPE_ID = 2;

	@Override
	@Transactional
	public RawMaterialMasterResponseDto addOrUpdateRawMaterial(@Valid RawMaterialMasterRequestDto request, long id) {

		UserMasterEntity user = getUser(request.getUserId());
		UnitMasterEntity unit = getUnit(request.getUnitId());
		RawMaterialCategoryMasterEntity category = getCategory(request.getRawMaterialCatId());

		validateDuplicate(request, user, unit, category, id);

		RawMaterialMasterEntity entity = (id == -1) ? createRawMaterial(request, user, unit, category)
				: updateRawMaterial(request, id, user, unit, category);

		handleCrockery(entity, user, category);
		handleFileUpload(request, user, entity);
		handleSuppliers(request, user, entity);

		return buildResponse(entity, user);
	}

	private UserMasterEntity getUser(Long userId) {
		return userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	private UnitMasterEntity getUnit(Long unitId) {
		return unitMasterRepository.findByIdAndIsDeleteFalse(unitId)
				.orElseThrow(() -> new RuntimeException("Unit not found"));
	}

	private RawMaterialCategoryMasterEntity getCategory(Long catId) {
		return rawMaterialCatMasterRepository.findByIdAndIsDeleteFalse(catId)
				.orElseThrow(() -> new RuntimeException("Category not found"));
	}

	private void validateDuplicate(RawMaterialMasterRequestDto request, UserMasterEntity user, UnitMasterEntity unit,
			RawMaterialCategoryMasterEntity category, long id) {

		Optional<RawMaterialMasterEntity> duplicate = rawMaterialMasterRepository
				.findByNameEnglishAndUserAndRawMaterialCatAndUnitAndIsDeleteFalse(request.getNameEnglish(), user,
						category, unit);

		if (duplicate.isPresent() && (id == -1 || !duplicate.get().getId().equals(id))) {
			throw new RuntimeException("Duplicate raw material");
		}
	}

	private RawMaterialMasterEntity createRawMaterial(RawMaterialMasterRequestDto request, UserMasterEntity user,
			UnitMasterEntity unit, RawMaterialCategoryMasterEntity category) {

		RawMaterialMasterEntity entity = rawMaterialMasterMapper.requestToEntity(request);
		entity.setIsActive(true);

		entity.setSequence(request.getSequence());
		entity.setUuid(databasePlanningService.getOrCreateUserUuid(user));
		entity.setUser(user);
		entity.setUnit(unit);
		entity.setRawMaterialCat(category);

		return rawMaterialMasterRepository.save(entity);
	}

	private RawMaterialMasterEntity updateRawMaterial(RawMaterialMasterRequestDto request, long id,
			UserMasterEntity user, UnitMasterEntity unit, RawMaterialCategoryMasterEntity category) {

		RawMaterialMasterEntity entity = Optional
				.ofNullable(rawMaterialMasterRepository.findByIdAndUserAndIsDeleteFalse(id, user))
				.orElseThrow(() -> new RuntimeException("Raw Material not found"));

		if (!isCrockeryCategory(category)) {
			crockeryCutleryRepository.deleteByRawMaterial(entity);
		}

		entity = rawMaterialMasterMapper.updateEntityFromRequest(request, entity);
		entity.setUpdatedAt(commonService.getCurrentDateTime());
		entity.setUser(user);
		entity.setSequence(request.getSequence());
		entity.setUnit(unit);
		entity.setRawMaterialCat(category);

		return rawMaterialMasterRepository.save(entity);
	}

	private void handleCrockery(RawMaterialMasterEntity entity, UserMasterEntity user,
			RawMaterialCategoryMasterEntity category) {

		if (!isCrockeryCategory(category))
			return;

		List<CrockeryCutleryEntity> list = crockeryCutleryRepository.findAllByRawMaterialAndUserAndIsDeleteFalse(entity,
				user);

		CrockeryCutleryEntity cc;

		if (list.size() == 1) {
			cc = list.get(0);
		} else {
			if (!list.isEmpty()) {
				crockeryCutleryRepository.deleteAll(list);
			}
			cc = new CrockeryCutleryEntity();
		}

		cc.setRawMaterial(entity);
		cc.setUser(user);
		cc.setRawMaterialNameEnglish(entity.getNameEnglish());
		cc.setRawMaterialNameHindi(entity.getNameHindi());
		cc.setRawMaterialNameGujarati(entity.getNameGujarati());
		cc.setRawMaterialCategory(category);

		crockeryCutleryRepository.save(cc);
	}

	private void handleFileUpload(RawMaterialMasterRequestDto request, UserMasterEntity user,
			RawMaterialMasterEntity entity) {

		if (request.getFile() == null || request.getFile().isEmpty())
			return;

		try {
			userFileService.storeFile(user.getId(), ModuleName.RAWMATERIAL.toString(), entity.getId(),
					FileType.IMAGE.toString(), request.getFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void handleSuppliers(RawMaterialMasterRequestDto request, UserMasterEntity user,
			RawMaterialMasterEntity entity) {

		List<RawMaterialSupplierRequestDto> suppliers = Optional.ofNullable(request.getRawMaterialSupplierRequestDtos())
				.orElse(Collections.emptyList());

		for (RawMaterialSupplierRequestDto req : suppliers) {

			PartyMasterEntity party = partyMasterRepository.findByIdAndIsDeleteFalse(req.getPartyId())
					.orElseThrow(() -> new RuntimeException("Party not found"));

			RawMaterialSupplierEntity supplier = (req.getId() == 0) ? new RawMaterialSupplierEntity()
					: Optional.ofNullable(rawMaterialSupplierRepository.findByIdAndIsDeleteFalse(req.getId()))
							.orElseThrow(() -> new RuntimeException("Supplier not found"));

			supplier.setParty(party);
			supplier.setRawMaterial(entity);
			supplier.setUser(user);
			supplier.setIsDefault(req.getIdDefault());
			supplier.setUpdatedAt(commonService.getCurrentDateTime());

			rawMaterialSupplierRepository.save(supplier);
		}
	}

	private RawMaterialMasterResponseDto buildResponse(RawMaterialMasterEntity entity, UserMasterEntity user) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		List<RawMaterialSupplierResponseDto> dtos = rawMaterialSupplierRepository
				.findAllByRawMaterialAndIsDeleteFalse(entity).stream().map(e -> {
					RawMaterialSupplierResponseDto dto = new RawMaterialSupplierResponseDto();
					dto.setId(e.getId());
					dto.setCreatedAt(e.getCreatedAt().format(formatter));
					dto.setIsDefault(e.getIsDefault());
					dto.setParty(partyMasterMapper.entityToResponse(e.getParty()));
					dto.setRawMaterialId(entity.getId());
					dto.setUserId(user.getId());
					return dto;
				}).collect(Collectors.toList());

		RawMaterialMasterResponseDto response = rawMaterialMasterMapper.entityToResponse(entity);
		response.setExpiryDate(entity.getExpiryDate() == null ? null : entity.getExpiryDate().format(dateFormatter));
		response.setRawMaterialSuppliers(dtos);

		return response;
	}

	@Override
	public Map<String, Object> getAllRawMaterialByUserId(Long userid, Long rawMateriaCatlId, String rawMaterialName,
			Boolean isActive, Long unitid, Integer pageNo, Integer pageSize, Boolean isAsc, Boolean isPurchaseApprove,
			Long purchaseApproveId) {

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		// Validate User
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userid)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userid));

		Long typeId = (rawMateriaCatlId == null || rawMateriaCatlId == 0) ? null : rawMateriaCatlId;
		Long filterUnitId = (unitid == null || unitid == 0) ? null : unitid;

		if (typeId != null && !rawMaterialCategoryMasterRepository.existsById(typeId)) {
			throw new RuntimeException("Raw Material Category not found with id: " + rawMateriaCatlId);
		}

		String searchName = (rawMaterialName == null || rawMaterialName.trim().isEmpty()) ? null
				: rawMaterialName.trim();

		Sort sort;

		if (isAsc == null) {
			// Default
			sort = Sort.by("sequence").ascending();
		} else {
			if(isPurchaseApprove == null || !isPurchaseApprove) {
				sort = isAsc ? Sort.by("name_english").ascending() : Sort.by("name_english").descending();
			}else {
				sort = isAsc ? Sort.by("rawMaterial.nameEnglish").ascending() : Sort.by("rawMaterial.nameEnglish").descending();
			}
		}

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

		List<RawMaterialMasterResponseDto> dtoList = new ArrayList<>();
		Map<String, Object> rawMatResponse = new HashMap<>();
		
		if(isPurchaseApprove == null || !isPurchaseApprove) {
			// Fetch paginated data
			Page<RawMaterialMasterEntity> pageResult = rawMaterialMasterRepository.searchByNameOrType(user.getId(), typeId,
					isActive, filterUnitId, searchName, pageable);
	
			for (RawMaterialMasterEntity entity : pageResult.getContent()) {
	
				RawMaterialMasterResponseDto dto = rawMaterialMasterMapper.entityToResponse(entity);
				dto.setExpiryDate(entity.getExpiryDate() == null ? null : entity.getExpiryDate().format(dateFormatter));
				dto.setFile(environment.getProperty("app.image.url") + entity.getFile());
				RawMaterialCategoryMasterEntity categoryMasterEntity = rawMaterialCategoryMasterRepository
						.findByIdAndUserAndIsDeleteFalse(entity.getRawMaterialCat().getId(), user);
				RawMaterialCategoryMasterResponseDto categoryMasterResponseDto = new RawMaterialCategoryMasterResponseDto();
	
				categoryMasterResponseDto.setId(categoryMasterEntity.getId());
				categoryMasterResponseDto.setNameEnglish(categoryMasterEntity.getNameEnglish());
				categoryMasterResponseDto.setNameGujarati(categoryMasterEntity.getNameGujarati());
				categoryMasterResponseDto.setNameHindi(categoryMasterEntity.getNameHindi());
				categoryMasterResponseDto.setSequence(categoryMasterEntity.getSequence());
				categoryMasterResponseDto.setIsDirect(categoryMasterEntity.getIsDirect());
				categoryMasterResponseDto.setCreatedAt(categoryMasterEntity.getCreatedAt().format(formatter));
				categoryMasterResponseDto.setUserId(categoryMasterEntity.getUser().getId());
	
				dto.setRawMaterialCat(categoryMasterResponseDto);
	
				UnitHierarchyDto unitHierarchyDto = unitMasterService
						.getParentUnitsWithChildren(entity.getUnit() == null ? null : entity.getUnit().getId());
	
				if (entity.getCreatedAt() != null) {
					dto.setCreatedAt(entity.getCreatedAt().format(formatter));
				}
	
				dto.setUnitHierarchy(unitHierarchyDto);
	
				// Fetch suppliers
				List<RawMaterialSupplierEntity> suppliers = rawMaterialSupplierRepository
						.findAllByRawMaterialAndIsDeleteFalse(entity);
	
				List<RawMaterialSupplierResponseDto> supplierDtos = new ArrayList<>();
	
				for (RawMaterialSupplierEntity supplierEntity : suppliers) {
					RawMaterialSupplierResponseDto supplierDto = new RawMaterialSupplierResponseDto();
	
					supplierDto.setId(supplierEntity.getId());
					supplierDto.setCreatedAt(supplierEntity.getCreatedAt().format(formatter));
					supplierDto.setIsDefault(supplierEntity.getIsDefault());
					supplierDto.setParty(partyMasterMapper.entityToResponse(supplierEntity.getParty()));
					supplierDto.setRawMaterialId(supplierEntity.getRawMaterial().getId());
					supplierDto.setUserId(supplierEntity.getUser().getId());
	
					supplierDtos.add(supplierDto);
				}
	
				dto.setRawMaterialSuppliers(supplierDtos);
				// ── Closing Stock Calculation ─────────────────────────────────────────
				try {
				    LocalDate from = LocalDate.of(2000, 1, 1); // full history
				    LocalDate to   = LocalDate.now();
	
				    Long rmId = entity.getId();
	
				    // ── Resolve unit conversion ───────────────────────────────────────
				    boolean hasParentUnit = false;
				    UnitMasterEntity unitForConversion = null;
	
				    if (entity.getUnit() != null) {
				        Optional<UnitMasterEntity> rmUnitOp =
				                unitMasterRepository.findByIdAndIsParentUnitFalse(entity.getUnit().getId());
	
				        if (rmUnitOp.isPresent()
				                && rmUnitOp.get().getParentUnit() != null
				                && rmUnitOp.get().getEquivalentValue() != null
				                && rmUnitOp.get().getEquivalentValue() != 0.0) {
				            hasParentUnit     = true;
				            unitForConversion = rmUnitOp.get();
				        }
				    }
	
				    // ── OPB ───────────────────────────────────────────────────────────
				    double opb = entity.getOpbStock() != null
				            ? entity.getOpbStock().doubleValue() : 0.0;
				    if (hasParentUnit) {
				        opb = convertUnitQtyToParentQty(unitForConversion, opb);
				    }
	
				    // ── Purchase ──────────────────────────────────────────────────────
				    double purchase = stockLedgerRepository
				            .sumQtyInByRawMaterialAndDateRangeAndRefType(rmId, from, to, "PURCHASE");
				    if (hasParentUnit) {
				        purchase = convertUnitQtyToParentQty(unitForConversion, purchase);
				    }
	
				    // ── Purchase Return ───────────────────────────────────────────────
				    double purchaseReturn = stockLedgerRepository
				            .sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to, "PURCHASE_RETURN");
				    if (hasParentUnit) {
				        purchaseReturn = convertUnitQtyToParentQty(unitForConversion, purchaseReturn);
				    }
	
				    // ── Sell (Store Issue + Chef Requisition + SOT Store Issue) ───────
				    double sell = stockLedgerRepository
				                    .sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to, "STORE_ISSUE")
				            + stockLedgerRepository
				                    .sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to, "CHEF_REQUISITION")
				            + stockLedgerRepository
				                    .sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to, "SOT_STORE_ISSUE");
				    if (hasParentUnit) {
				        sell = convertUnitQtyToParentQty(unitForConversion, sell);
				    }
	
				    // ── Sell Return ───────────────────────────────────────────────────
				    double sellReturn = stockLedgerRepository
				                    .sumQtyInByRawMaterialAndDateRangeAndRefType(rmId, from, to, "STORE_ISSUE_RETURN")
				            + stockLedgerRepository
				                    .sumQtyInByRawMaterialAndDateRangeAndRefType(rmId, from, to, "SOT_STORE_RETURN");
				    if (hasParentUnit) {
				        sellReturn = convertUnitQtyToParentQty(unitForConversion, sellReturn);
				    }
				    
				 // Also include previous store manage adjustments
					double smIncrease = stockLedgerRepository.sumQtyInByRawMaterialAndDateRangeAndRefType(rmId, from, to,
							"INCREASE") ;
					if (hasParentUnit)
						smIncrease = convertUnitQtyToParentQty(unitForConversion, smIncrease);

					double smWastage =  stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefType(rmId, from, to,
							"WASTAGE") ;
					if (hasParentUnit)
						smWastage = convertUnitQtyToParentQty(unitForConversion, smWastage);
	
				    // ── Final closing stock ───────────────────────────────────────────
				    double closingStock = opb + purchase - purchaseReturn - sell + sellReturn+ smIncrease - smWastage;
				    closingStock = BigDecimal.valueOf(closingStock)
				            .setScale(2, RoundingMode.HALF_UP).doubleValue();
	
				    dto.setClosingStock(BigDecimal.valueOf(closingStock));
	
				} catch (Exception e) {
				    dto.setClosingStock(BigDecimal.ZERO); // fallback — don't break the list
				}
				// ─────────────────────────────────────────────────────────────────────
	
				dtoList.add(dto);
				
			}
			rawMatResponse.put("totalItems", pageResult.getTotalElements());
			rawMatResponse.put("totalPages", pageResult.getTotalPages());
		}else {
			Page<PurchaseRequestDetailsEntity> pageResult = purchaseRequestDetailsRepository
					.findApprovedRawMaterials(purchaseApproveId, "APPROVED", typeId, filterUnitId, searchName, pageable);

			for (PurchaseRequestDetailsEntity detail : pageResult.getContent()) {

				RawMaterialMasterEntity entity = detail.getRawMaterial();

				RawMaterialMasterResponseDto dto = rawMaterialMasterMapper.entityToResponse(entity);

				dto.setExpiryDate(entity.getExpiryDate() == null ? null : entity.getExpiryDate().format(dateFormatter));

				dto.setFile(environment.getProperty("app.image.url") + entity.getFile());

				RawMaterialCategoryMasterEntity categoryMasterEntity = rawMaterialCategoryMasterRepository
						.findByIdAndUserAndIsDeleteFalse(entity.getRawMaterialCat().getId(), user);

				RawMaterialCategoryMasterResponseDto categoryMasterResponseDto = new RawMaterialCategoryMasterResponseDto();

				categoryMasterResponseDto.setId(categoryMasterEntity.getId());
				categoryMasterResponseDto.setNameEnglish(categoryMasterEntity.getNameEnglish());
				categoryMasterResponseDto.setNameGujarati(categoryMasterEntity.getNameGujarati());
				categoryMasterResponseDto.setNameHindi(categoryMasterEntity.getNameHindi());
				categoryMasterResponseDto.setSequence(categoryMasterEntity.getSequence());
				categoryMasterResponseDto.setIsDirect(categoryMasterEntity.getIsDirect());
				categoryMasterResponseDto.setCreatedAt(categoryMasterEntity.getCreatedAt().format(formatter));
				categoryMasterResponseDto.setUserId(categoryMasterEntity.getUser().getId());

				dto.setRawMaterialCat(categoryMasterResponseDto);

				UnitHierarchyDto unitHierarchyDto = unitMasterService
						.getParentUnitsWithChildren(entity.getUnit() == null ? null : entity.getUnit().getId());

				if (entity.getCreatedAt() != null) {
					dto.setCreatedAt(entity.getCreatedAt().format(formatter));
				}

				dto.setUnitHierarchy(unitHierarchyDto);

				// Existing supplier logic
				List<RawMaterialSupplierEntity> suppliers = rawMaterialSupplierRepository
						.findAllByRawMaterialAndIsDeleteFalse(entity);

				List<RawMaterialSupplierResponseDto> supplierDtos = new ArrayList<>();

				for (RawMaterialSupplierEntity supplierEntity : suppliers) {

					RawMaterialSupplierResponseDto supplierDto = new RawMaterialSupplierResponseDto();

					supplierDto.setId(supplierEntity.getId());
					supplierDto.setCreatedAt(supplierEntity.getCreatedAt().format(formatter));
					supplierDto.setIsDefault(supplierEntity.getIsDefault());
					supplierDto.setParty(partyMasterMapper.entityToResponse(supplierEntity.getParty()));
					supplierDto.setRawMaterialId(supplierEntity.getRawMaterial().getId());
					supplierDto.setUserId(supplierEntity.getUser().getId());

					supplierDtos.add(supplierDto);
				}

				dto.setRawMaterialSuppliers(supplierDtos);

				dto.setClosingStock(detail.getApprovedQty());

				dtoList.add(dto);
			}
			rawMatResponse.put("totalItems", pageResult.getTotalElements());
			rawMatResponse.put("totalPages", pageResult.getTotalPages());
		}
		
		rawMatResponse.put("Raw Material Details", dtoList);
		rawMatResponse.put("currentPage", pageNo);

		Map<String, Object> response = new HashMap<>();
		response.put("data", rawMatResponse);
		response.put("success", true);
		response.put("msg", "Raw materials fetched successfully");

		return response;
	}

	 private Double convertUnitQtyToParentQty(UnitMasterEntity unit, Double qty) {
	        if (qty == null || qty == 0.0) return 0.0;
	        return BigDecimal.valueOf(qty)
	                .divide(BigDecimal.valueOf(unit.getEquivalentValue()), 2, RoundingMode.HALF_UP)
	                .doubleValue();
	    }
	 
	@Override
	public RawMaterialMasterResponseDto getRawMaterialById(Long id) {

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		RawMaterialMasterEntity entity = rawMaterialMasterRepository.findByIdAndIsDeleteFalse(id);
		if (entity != null) {
			RawMaterialMasterResponseDto dto = rawMaterialMasterMapper.entityToResponse(entity);
			dto.setFile(environment.getProperty("app.image.url") + entity.getFile());
			if (entity.getCreatedAt() != null) {
				dto.setCreatedAt(entity.getCreatedAt().format(formatter));
			}
			dto.setExpiryDate(entity.getExpiryDate() == null ? null : entity.getExpiryDate().format(dateFormatter));
			List<RawMaterialSupplierResponseDto> supplierDtos = new ArrayList<>();
			List<RawMaterialSupplierEntity> suppliers = rawMaterialSupplierRepository
					.findAllByRawMaterialAndIsDeleteFalse(entity);
			for (RawMaterialSupplierEntity supplierEntity : suppliers) {
				RawMaterialSupplierResponseDto supplierDto = new RawMaterialSupplierResponseDto();
				supplierDto.setId(supplierEntity.getId());
				supplierDto.setCreatedAt(supplierEntity.getCreatedAt().format(formatter));
				supplierDto.setIsDefault(supplierEntity.getIsDefault());
				supplierDto.setParty(partyMasterMapper.entityToResponse(supplierEntity.getParty()));
				supplierDto.setRawMaterialId(supplierEntity.getRawMaterial().getId());
				supplierDto.setUserId(supplierEntity.getId());
				supplierDtos.add(supplierDto);
			}
			dto.setRawMaterialSuppliers(supplierDtos);

			return dto;
		} else {
			return null;
		}
	}

	@Override
	public Boolean deleteRawMaterialById(Long id) {
		if (rawMaterialMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			RawMaterialMasterEntity entity = rawMaterialMasterRepository.findByIdAndIsDeleteFalse(id);

			if (itemRawMaterialRepository.findAllByRawMaterialAndIsDeleteFalse(entity).size() > 0) {
				throw new RuntimeException("Please Delete Menu Item Raw Material First");
			}
			entity.setIsDelete(true);
			List<RawMaterialSupplierEntity> entities = rawMaterialSupplierRepository
					.findAllByRawMaterialAndIsDeleteFalse(entity);
			for (RawMaterialSupplierEntity rawMaterialSupplierEntity : entities) {
				rawMaterialSupplierEntity.setIsDelete(true);
				rawMaterialSupplierRepository.save(rawMaterialSupplierEntity);
			}
			rawMaterialMasterRepository.save(entity);

			Optional<CrockeryCutleryEntity> crockeryCutleryEntity = crockeryCutleryRepository
					.findByRawMaterialIdAndIsDeleteFalse(id);

			if (crockeryCutleryEntity.isPresent()) {
				crockeryCutleryRepository.deleteById(crockeryCutleryEntity.get().getId());
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean updateRawMaterialStatus(Long id, Boolean isActive) {
		if (rawMaterialMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			RawMaterialMasterEntity entity = rawMaterialMasterRepository.findByIdAndIsDeleteFalse(id);
			entity.setIsActive(isActive);
			rawMaterialMasterRepository.save(entity);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Boolean updateSequence(@Valid List<UpdateRawMaterialRequestDto> request) {
		if (request.isEmpty()) {
			return false;
		}

		List<Long> ids = request.stream().map(UpdateRawMaterialRequestDto::getRawMaterialId)
				.collect(Collectors.toList());

		List<RawMaterialMasterEntity> entities = rawMaterialMasterRepository.findAllByIdInAndIsDeleteFalse(ids);

		if (entities.isEmpty()) {
			return false;
		}

		Map<Long, Integer> idToSequenceMap = request.stream().collect(Collectors
				.toMap(UpdateRawMaterialRequestDto::getRawMaterialId, UpdateRawMaterialRequestDto::getSequence));

		entities.forEach(entity -> entity.setSequence(idToSequenceMap.get(entity.getId())));

		rawMaterialMasterRepository.saveAll(entities);

		Set<Long> foundIds = entities.stream().map(RawMaterialMasterEntity::getId).collect(Collectors.toSet());
		request.stream().map(UpdateRawMaterialRequestDto::getRawMaterialId).filter(id -> !foundIds.contains(id))
				.forEach(id -> log.warn("RawMaterial with id {} not found or deleted", id));

		return true;
	}

	@Override
	public Map<String, Object> getAllRawMaterialByUserId(Long userid, String rawMaterialName, Boolean isActive) {
		// Validate User
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userid)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userid));

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		List<RawMaterialMasterEntity> pageResult = rawMaterialMasterRepository.searchByNameOrType(user.getId(),
				isActive);

		List<RawMaterialInMenuItemResponseDto> dtoList = new ArrayList<>();

		for (RawMaterialMasterEntity entity : pageResult) {

			RawMaterialInMenuItemResponseDto dto = rawMaterialMasterMapper.entityToResponse2(entity);

			System.out.println(entity.getUnit().getId());
			UnitHierarchyDto unitHierarchyDto = unitMasterService
					.getParentUnitsWithChildren(entity.getUnit() == null ? null : entity.getUnit().getId());

			dto.setUnitHierarchy(unitHierarchyDto);
			dtoList.add(dto);
		}

		// Build Response
		Map<String, Object> rawMatResponse = new HashMap<>();
		rawMatResponse.put("Raw Material Details", dtoList);
		Map<String, Object> response = new HashMap<>();
		response.put("data", rawMatResponse);
		response.put("success", true);
		response.put("msg", "Raw materials fetched successfully");

		return response;
	}

	@Override
	public Map<String, Object> getAllGeneralFix(Long userId, String rawMaterialName, Boolean isGeneralFix, int page,
			int size) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		Pageable pageable = PageRequest.of(page, size);

		Page<RawMaterialMasterEntity> pageResult = rawMaterialMasterRepository.searchByNameOrIsGeneralFix(user.getId(),
				isGeneralFix, pageable);

		List<RawMaterialInMenuItemResponseDto> dtoList = new ArrayList<>();

		for (RawMaterialMasterEntity entity : pageResult.getContent()) {

			RawMaterialInMenuItemResponseDto dto = rawMaterialMasterMapper.entityToResponse2(entity);
			dtoList.add(dto);
		}

		Map<String, Object> rawMatResponse = new HashMap<>();
		rawMatResponse.put("items", dtoList);
		rawMatResponse.put("currentPage", pageResult.getNumber());
		rawMatResponse.put("totalItems", pageResult.getTotalElements());
		rawMatResponse.put("totalPages", pageResult.getTotalPages());
		rawMatResponse.put("size", pageResult.getSize());

		Map<String, Object> response = new HashMap<>();
		response.put("data", rawMatResponse);
		response.put("success", true);
		response.put("msg", "Raw materials fetched successfully");

		return response;
	}

	@Override
	@Transactional
	public Boolean updateRawMaterialItemCategory(List<Long> rawMaterialIds, Long newCatId, Long userId) {

		if (rawMaterialIds == null || rawMaterialIds.isEmpty() || newCatId == null) {
			throw new IllegalArgumentException("Invalid input.");
		}

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User Not Found"));

		RawMaterialCategoryMasterEntity category = rawMaterialCatMasterRepository
				.findByIdAndUserAndIsDeleteFalse(newCatId, user);

		if (category == null) {
			throw new RuntimeException("Category not found");
		}

		int updatedRows = rawMaterialMasterRepository.updateRawMaterialItemCategory(rawMaterialIds, newCatId, userId);

		// 🔥 If not crockery → only delete (no extra DB fetch)
		if (!isCrockeryCategory(category)) {
			crockeryCutleryRepository.deleteAllByRawMaterialCategory_Id(category.getId());
			return updatedRows > 0;
		}

		// 🔥 Fetch only when required
		List<CrockeryCutleryEntity> crockeryList = rawMaterialMasterRepository
				.findAllByIdInAndIsDeleteFalse(rawMaterialIds).stream()
				.map(rm -> mapToCrockeryEntity(rm, user, category)).collect(Collectors.toList());

		// 🔥 Save only if data exists
		if (!crockeryList.isEmpty()) {
			crockeryCutleryRepository.saveAll(crockeryList);
		}

		return updatedRows > 0;
	}

	private CrockeryCutleryEntity mapToCrockeryEntity(RawMaterialMasterEntity rm, UserMasterEntity user,
			RawMaterialCategoryMasterEntity category) {
		CrockeryCutleryEntity entity = new CrockeryCutleryEntity();
		entity.setRawMaterial(rm);
		entity.setUser(user);
		entity.setRawMaterialNameEnglish(rm.getNameEnglish());
		entity.setRawMaterialNameHindi(rm.getNameHindi());
		entity.setRawMaterialNameGujarati(rm.getNameGujarati());
		entity.setRawMaterialCategory(category);
		return entity;
	}

	private boolean isCrockeryCategory(RawMaterialCategoryMasterEntity category) {
		return category.getRawMaterialCatType() != null && CROCKERY_TYPE_ID == category.getRawMaterialCatType().getId();
	}

	@Override
	@Transactional
	public Boolean updateRawMaterialSupplier(List<Long> rawMaterialIds, Long newSupplierId, Long userId) {

		if (rawMaterialIds == null || newSupplierId == null || userId == null) {
			throw new IllegalArgumentException("ID cannot be null");
		}

		PartyMasterEntity partyMasterEntity = partyMasterRepository.findByIdAndIsDeleteFalse(newSupplierId)
				.orElseThrow(() -> new RuntimeException("Supplier not found with id : " + newSupplierId));
		UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

		for (Long id : rawMaterialIds) {
			System.out.println("id:-" + id);
			Optional<RawMaterialSupplierEntity> entityOptioal = Optional
					.ofNullable(rawMaterialSupplierRepository.findByIdAndIsDeleteFalse(id));
			if (entityOptioal.isPresent()) {
				System.out.println("in after");
				RawMaterialSupplierEntity entity = entityOptioal.get();
				entity.setParty(partyMasterEntity);
				entity.setUpdatedAt(commonService.getCurrentDateTime());
				entity.setIsDefault(true);
				rawMaterialSupplierRepository.save(entity);
			} else {
				System.out.println("in before");
				RawMaterialMasterEntity masterEntity = Optional
						.ofNullable(rawMaterialMasterRepository.findByIdAndIsDeleteFalse(id))
						.orElseThrow(() -> new RuntimeException("Raw material not found with id : " + id));

				RawMaterialSupplierEntity entity = new RawMaterialSupplierEntity();

				entity.setRawMaterial(masterEntity);
				entity.setParty(partyMasterEntity);
				entity.setUser(userMasterEntity);
				entity.setIsDefault(true);

				rawMaterialSupplierRepository.save(entity);
			}
		}

		return true;
	}

	@Override
	public Page<RawMaterialCategoryChangeResponseDto> getRawMaterialItemByCategory(List<Long> catIds, Long userId,
			Pageable pageable) {

		Page<Object[]> entities = rawMaterialMasterRepository.findByRawMaterialCatIdInAndUserIdAndIsDeleteFalse(catIds,
				userId, pageable);

		return entities.map(this::mapToResponse);
	}

	public RawMaterialCategoryChangeResponseDto mapToResponse(Object[] row) {
		RawMaterialCategoryChangeResponseDto dto = new RawMaterialCategoryChangeResponseDto();

		int index = 0;

		dto.setId(commonService.getLong(row[index++]));
		dto.setNameEnglish(commonService.getString(row[index++]));
		dto.setNameHindi(commonService.getString(row[index++]));
		dto.setNameGujarati(commonService.getString(row[index++]));
		dto.setRawMaterialCatId(commonService.getLong(row[index++]));
		dto.setRawMaterialCatNameEnglish(commonService.getString(row[index++]));
		dto.setRawMaterialCatNameHindi(commonService.getString(row[index++]));
		dto.setRawMaterialCatNameGujarati(commonService.getString(row[index++]));
		dto.setPartyId(commonService.getLong(row[index++]));
		dto.setPartyNameEnglish(commonService.getString(row[index++]));
		dto.setPartyNameHindi(commonService.getString(row[index++]));
		dto.setPartyNameGujarati(commonService.getString(row[index++]));
		dto.setUnit(commonService.getLong(row[index++]));
		dto.setUser(commonService.getLong(row[index++]));

		return dto;
	}
}
