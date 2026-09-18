package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.PurchaseRequestDetailsEntity;
import com.crmportal.entity.PurchaseRequestEntity;
import com.crmportal.entity.RawMaterialCategoryMasterEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.PurchaseRequestDetailsRepository;
import com.crmportal.repository.PurchaseRequestRepository;
import com.crmportal.repository.RawMaterialCategoryMasterRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.PurchaseRequestDetailsDto;
import com.crmportal.request.dto.PurchaseRequestDto;
import com.crmportal.response.dto.PurchaseRequestDetailsResponseDto;
import com.crmportal.response.dto.PurchaseRequestResponseDto;
import com.crmportal.response.dto.UnitHierarchyDto;
import com.crmportal.service.PurchaseApprovalService;
import com.crmportal.service.UnitMasterService;
import com.crmportal.utility.DateUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseApprovalServiceImpl implements PurchaseApprovalService {

	private final PurchaseRequestRepository purchaseRequestRepository;
	private final PurchaseRequestDetailsRepository purchaseRequestDetailsRepository;
	private final RawMaterialMasterRepository rawMaterialMasterRepository;
	private final UnitMasterRepository unitMasterRepository;
	private final PurchaseOrderStoreServiceImpl purchaseOrderStoreServiceImpl;
	private final UserMasterRepository userMasterRepository;
	private final RawMaterialCategoryMasterRepository rawMaterialCategoryMasterRepository;
	private final UnitMasterService unitMasterService;
	
	@Override
	public String generatePurchaseRequestCode(Long userId) {
		int year = LocalDate.now().getYear();

		String prefix = "PR-" + year + "-";

		Long count = purchaseRequestRepository.countByUserIdAndPrefix(userId, prefix);

		long nextNo = count + 1;

		return String.format("%s%03d", prefix, nextNo);
	}

	@Override
	@Transactional
	public Boolean addUpdatePurchaseRequest(PurchaseRequestDto request) {
		if (request == null) {
			return false;
		}

		if(request.getStatus().equalsIgnoreCase("APPROVED") && request.getApprovedBy() == null) {
			throw new RuntimeException("Approved By Id is required.");
		}

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));
		
		Long adminId;
		
		if(user.getClientId() == 0) {
			adminId = request.getUserId();
		}else {
			adminId = user.getClientId();
		}
		
		PurchaseRequestEntity parentEntity;
		boolean isNewParent = isNewRecord(request.getId());

		String requestCode = request.getRequestCode().trim();
		
		// 1. Save or Fetch Parent Entity
		if (isNewParent) {
			if (purchaseRequestRepository.existsByRequestCodeAndAdminIdAndIsDeleteFalse(requestCode, adminId)) {
				throw new IllegalArgumentException("Request Code already exists: " + requestCode);
			}
			
			parentEntity = new PurchaseRequestEntity();
			parentEntity.setIsDelete(false);
		} else {
			parentEntity = purchaseRequestRepository.findByIdAndIsDeleteFalse(request.getId()).orElseThrow(
					() -> new EntityNotFoundException("Purchase Request not found with ID: " + request.getId()));
			
			if (purchaseRequestRepository.existsByRequestCodeAndIdNotAndAdminIdAndIsDeleteFalse(requestCode, request.getId(), adminId)) {
				throw new IllegalArgumentException("Request Code already exists: " + requestCode);
			}
			 
			parentEntity.setUpdatedAt(LocalDateTime.now());
		}

		parentEntity.setRequestCode(request.getRequestCode());
		parentEntity.setStartDate(DateUtils.parseDate(request.getStartDate()));
		parentEntity.setEndDate(DateUtils.parseDate(request.getEndDate()));
		parentEntity.setStatus(request.getStatus());
		parentEntity.setRemarks(request.getRemarks());
		parentEntity.setUserId(request.getUserId());
		parentEntity.setApprovedBy(request.getApprovedBy());
		parentEntity.setAdminId(adminId);

		// Save parent first to generate its primary key if new
		parentEntity = purchaseRequestRepository.save(parentEntity);

		// 2. Handle Child Details
		if (request.getRequestDetails() != null) {

			// For updates, handle deletion of removed table rows
			if (!isNewParent) {
				List<PurchaseRequestDetailsEntity> existingDetails = purchaseRequestDetailsRepository
						.findByPurchaseRequest(parentEntity);

				Set<Long> incomingDetailIds = request.getRequestDetails().stream().map(PurchaseRequestDetailsDto::getId)
						.filter(id -> !isNewRecord(id)).collect(Collectors.toSet());

				List<PurchaseRequestDetailsEntity> detailsToDelete = existingDetails.stream()
						.filter(detail -> !incomingDetailIds.contains(detail.getId())).collect(Collectors.toList());

				if (!detailsToDelete.isEmpty()) {
					purchaseRequestDetailsRepository.deleteAll(detailsToDelete);
				}
			}

			// Save or update incoming child items
			List<PurchaseRequestDetailsEntity> detailsToSave = new ArrayList<>();

			for (PurchaseRequestDetailsDto detailDto : request.getRequestDetails()) {
				if(detailDto.getRequestQty().compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}
				PurchaseRequestDetailsEntity detailEntity;

				if (isNewRecord(detailDto.getId())) {
					detailEntity = new PurchaseRequestDetailsEntity();
				} else {
					detailEntity = purchaseRequestDetailsRepository.findByIdAndIsDeleteFalse(detailDto.getId()).orElseThrow(
							() -> new EntityNotFoundException("Detail row not found with ID: " + detailDto.getId()));
				}

				RawMaterialMasterEntity rawMaterial = rawMaterialMasterRepository.findByIdAndIsDeleteFalse(detailDto.getRawMaterialId());
				
				if(rawMaterial == null) {
					throw new RuntimeException("Raw Material not found with ID: " + detailDto.getRawMaterialId());
				}

				UnitMasterEntity requestQtyUnit = unitMasterRepository.findByIdAndIsDeleteFalse(detailDto.getRequestQtyUnitId()).orElseThrow(
						() -> new EntityNotFoundException("Request Unit not found with ID: " + detailDto.getRequestQtyUnitId()));
				
				if(request.getStatus().equalsIgnoreCase("APPROVED")) {
					UnitMasterEntity approvedQtyUnit = unitMasterRepository.findByIdAndIsDeleteFalse(detailDto.getApprovedQtyUnitId()).orElseThrow(
							() -> new EntityNotFoundException("Approved Unit not found with ID: " + detailDto.getApprovedQtyUnitId()));
					detailEntity.setApprovedQty(detailDto.getApprovedQty());
					detailEntity.setApprovedQtyUnit(approvedQtyUnit);
				}
				detailEntity.setPurchaseRequest(parentEntity);
				detailEntity.setRawMaterial(rawMaterial);
				detailEntity.setRequestQty(detailDto.getRequestQty());
				detailEntity.setRequestQtyUnit(requestQtyUnit);

				detailsToSave.add(detailEntity);
			}

			purchaseRequestDetailsRepository.saveAll(detailsToSave);
		}

		return true;
	}

	@Override
	public PurchaseRequestResponseDto getPurchaseRequestById(Long purchaseRequestId) {
		PurchaseRequestEntity entity = purchaseRequestRepository.findById(purchaseRequestId).orElseThrow(
				() -> new EntityNotFoundException("Purchase Request not found with ID: " + purchaseRequestId));

		UserMasterEntity approvedBy = userMasterRepository.findByIdAndIsDeleteFalse(entity.getApprovedBy())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + entity.getApprovedBy()));
		
		List<PurchaseRequestDetailsEntity> detailsEntities = purchaseRequestDetailsRepository
				.findByPurchaseRequest(entity);

		String startDate = entity.getStartDate() != null ? DateUtils.formatLocalDate(entity.getStartDate()) : null;
		String endDate = entity.getEndDate() != null ? DateUtils.formatLocalDate(entity.getEndDate()) : null;
		
		Long totalDays = calculateDaysBetween(startDate, endDate);
		
		// 3. Map child entities to PurchaseRequestDetailsResponseDto
		List<PurchaseRequestDetailsResponseDto> detailsDtos = detailsEntities.stream().map(detail -> mapToDetailsDto(detail, totalDays.intValue()))
				.collect(Collectors.toList());

		// 4. Map parent entity to PurchaseRequestResponseDto
		return PurchaseRequestResponseDto.builder().id(entity.getId()).requestCode(entity.getRequestCode())
				.startDate(startDate).endDate(endDate).status(entity.getStatus()).remarks(entity.getRemarks())
				.userId(entity.getUserId()).isDelete(entity.getIsDelete())
				.createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
				.updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
				.requestDetails(detailsDtos).approvedBy(entity.getApprovedBy())
				.approvedByName(approvedBy.getFirstName() + " " + approvedBy.getLastName()).build();
	}

	@Override
	public List<PurchaseRequestResponseDto> getAllPurchaseRequests(Long userId, String status) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));
		
		List<PurchaseRequestEntity> requests = purchaseRequestRepository.findByAllPurchaseRequest(userId, status);			
		
		if (requests.isEmpty()) {
			return Collections.emptyList();
		}

		return requests.stream().map(entity -> {

			UserMasterEntity approvedBy = null;

			if (entity.getApprovedBy() != null) {
				approvedBy = userMasterRepository.findByIdAndIsDeleteFalse(entity.getApprovedBy()).orElse(null);
			}
			
			UserMasterEntity requestBy = userMasterRepository.findByIdAndIsDeleteFalse(entity.getUserId()).orElse(null);
			String requestByName = "";
			
			if(requestBy != null) {
				requestByName = requestBy.getFirstName() + " " + requestBy.getLastName();
			}

			String approvedByName = null;

			if (approvedBy != null) {
				approvedByName = approvedBy.getFirstName() + " " + approvedBy.getLastName();
			}

			return PurchaseRequestResponseDto.builder().id(entity.getId()).requestCode(entity.getRequestCode())
					.startDate(entity.getStartDate() != null ? DateUtils.formatLocalDate(entity.getStartDate()) : null)
					.endDate(entity.getEndDate() != null ? DateUtils.formatLocalDate(entity.getEndDate()) : null).status(entity.getStatus())
					.remarks(entity.getRemarks()).userId(entity.getUserId()).isDelete(entity.getIsDelete())
					.createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
					.updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
					.requestDetails(null).approvedBy(entity.getApprovedBy()).approvedByName(approvedByName)
					.requestBy(requestByName)
					.build();
		}).collect(Collectors.toList());
	}

	@Override
	public Page<PurchaseRequestDetailsResponseDto> getDetailsByRawMaterialId(Long rawMaterialCatId, String startDateStr,
			String endDateStr, int page, int size, String rawMaterialName, Long purchaseRequestId, Boolean isAllData) {

		RawMaterialCategoryMasterEntity rawMaterialCat = rawMaterialCategoryMasterRepository
				.findByIdAndIsDeleteFalse(rawMaterialCatId).orElseThrow(
						() -> new RuntimeException("Raw Material Category not found with id: " + rawMaterialCatId));

		List<RawMaterialMasterEntity> rawMaterials = rawMaterialMasterRepository
				.findAllByRawMaterialCatAndIsDeleteFalse(rawMaterialCat);

		if (rawMaterialName != null && !rawMaterialName.trim().isEmpty()) {
			String searchName = rawMaterialName.trim().toLowerCase();

			rawMaterials = rawMaterials.stream()
					.filter(rawMaterial -> rawMaterial.getNameEnglish() != null
							&& rawMaterial.getNameEnglish().toLowerCase().contains(searchName))
					.collect(Collectors.toList());
		}
		
		if (rawMaterials.isEmpty()) {
			return new PageImpl<>(Collections.emptyList());
		}
		
		Pageable pageable = PageRequest.of(page, size);
		
		long totalDays = calculateDaysBetween(startDateStr, endDateStr);

		// Validate page and size
		if (page < 0) {
			page = 0;
		}

		if (size <= 0) {
			size = 100;
		}

		int start = page * size;

		if (start >= rawMaterials.size()) {
			return new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), rawMaterials.size());
		}

		// Fetch existing purchase request details for these raw materials in the date range
		List<PurchaseRequestDetailsEntity> existingDetails = purchaseRequestDetailsRepository
				.findByRawMaterialInAndPurchaseRequestIdAndIsDeleteFalseAndPurchaseRequestIsDeleteFalse(rawMaterials,
						purchaseRequestId);

	    // Map existing details by Raw Material ID for O(1) lookup
	    Map<Long, PurchaseRequestDetailsEntity> existingDetailsMap = existingDetails.stream()
	            .collect(Collectors.toMap(
	                    detail -> detail.getRawMaterial().getId(),
	                    detail -> detail,
	                    (existing, replacement) -> existing
	            ));
	    
	    if (!Boolean.TRUE.equals(isAllData)) {

	        rawMaterials = rawMaterials.stream()
	                .filter(rawMaterial ->
	                        existingDetailsMap.containsKey(rawMaterial.getId()))
	                .collect(Collectors.toList());
	    }

	    // After filtering, check if data exists
	    if (rawMaterials.isEmpty()) {
	        return new PageImpl<>(
	                Collections.emptyList(),
	                pageable,
	                0
	        );
	    }
	    
		int end = Math.min(start + size, rawMaterials.size());

		List<RawMaterialMasterEntity> paginatedRawMaterials = rawMaterials.subList(start, end);

		List<PurchaseRequestDetailsResponseDto> result = paginatedRawMaterials.stream().map(rawMaterial -> {

			BigDecimal avgDailyCons = parseBigDecimal(rawMaterial.getDailyConsumption());

			BigDecimal sysReqQty = avgDailyCons.multiply(BigDecimal.valueOf(totalDays));

			Long unitId = null;
			String unitName = null;
			
			Long approvedQtyUnitId = null;
			String approvedQtyUnitName = null;
			
			if (rawMaterial.getUnit() != null) {
				unitId = rawMaterial.getUnit().getId();
				unitName = rawMaterial.getUnit().getNameEnglish();
			}
			
			PurchaseRequestDetailsEntity existingDetail = existingDetailsMap.get(rawMaterial.getId());
			
			BigDecimal requestQty = BigDecimal.ZERO;
			BigDecimal approvedQty = BigDecimal.ZERO;
			Long detailId = null;
			
			if (existingDetail != null) {
	            detailId = existingDetail.getId();
	            
	            requestQty = existingDetail.getRequestQty();
	            approvedQty = existingDetail.getApprovedQty();
	            
				UnitMasterEntity requestQtyUnit = existingDetail.getRequestQtyUnit() != null
						? existingDetail.getRequestQtyUnit()
						: null;
				
				UnitMasterEntity approvedQtyUnit = existingDetail.getApprovedQtyUnit() != null
						? existingDetail.getApprovedQtyUnit()
						: null;

	            if (requestQtyUnit != null) {
	                unitId = requestQtyUnit.getId();
	                unitName = requestQtyUnit.getNameEnglish();
	            }
	            
	            if (approvedQtyUnit != null) {
	            	approvedQtyUnitId = approvedQtyUnit.getId();
	            	approvedQtyUnitName = approvedQtyUnit.getNameEnglish();
	            }
	        }
			
			BigDecimal todaysStock = BigDecimal
					.valueOf(purchaseOrderStoreServiceImpl.calculateClosingStock(rawMaterial));

			UnitHierarchyDto sysUnitHierarchyDto = unitMasterService
					.getParentUnitsWithChildren(rawMaterial.getUnit() == null ? null : rawMaterial.getUnit().getId());

			UnitHierarchyDto requestUnitHierarchyDto = unitMasterService
					.getParentUnitsWithChildren(unitId == null ? null : rawMaterial.getUnit().getId());
			
			UnitHierarchyDto approvedUnitHierarchyDto = unitMasterService
					.getParentUnitsWithChildren(approvedQtyUnitId == null ? null : rawMaterial.getUnit().getId());
			
			return PurchaseRequestDetailsResponseDto.builder().id(detailId) // Set existing detail record ID if present
					.rawMaterialId(rawMaterial.getId()).rawMaterialName(rawMaterial.getNameEnglish())
					.requestQtyUnitId(unitId).requestQtyUnitName(unitName)
					.sysUnit(rawMaterial.getUnit() != null ? rawMaterial.getUnit().getNameEnglish() : null)
					.avgDailyCons(avgDailyCons).leadTime(rawMaterial.getLeadTime()).minQty(rawMaterial.getMinStock())
					.maxQty(rawMaterial.getMaxStock()).todaysStock(todaysStock).sysReqQty(sysReqQty)
					.requestQty(requestQty).approvedQty(approvedQty).approvedQtyUnitId(approvedQtyUnitId)
					.approvedQtyUnitName(approvedQtyUnitName).sysUnitHierarchy(sysUnitHierarchyDto)
					.requestUnitHierarchy(requestUnitHierarchyDto).approvedUnitHierarchy(approvedUnitHierarchyDto)
					.build();

		}).collect(Collectors.toList());

		return new PageImpl<>(result, pageable, rawMaterials.size());
	}
	
	private PurchaseRequestDetailsResponseDto mapToDetailsDto(PurchaseRequestDetailsEntity detail, Integer totalDays) {
		PurchaseRequestDetailsResponseDto.PurchaseRequestDetailsResponseDtoBuilder builder = PurchaseRequestDetailsResponseDto
				.builder().id(detail.getId()).requestQty(detail.getRequestQty()).approvedQty(detail.getApprovedQty());

		if (detail.getRawMaterial() != null) {
			BigDecimal dailyConsumption = detail.getRawMaterial().getDailyConsumption() != null 
					? new BigDecimal(detail.getRawMaterial().getDailyConsumption()) 
					: BigDecimal.ZERO;
			
			UnitHierarchyDto sysQtyUnitHierarchy = unitMasterService
					.getParentUnitsWithChildren(detail.getRawMaterial().getId());
			
			builder.rawMaterialId(detail.getRawMaterial().getId());
			builder.rawMaterialName(detail.getRawMaterial().getNameEnglish());
			builder.avgDailyCons(dailyConsumption);
			builder.leadTime(detail.getRawMaterial().getLeadTime());
			builder.minQty(detail.getRawMaterial().getMinStock());
			builder.maxQty(detail.getRawMaterial().getMaxStock());
			builder.todaysStock(
					new BigDecimal(purchaseOrderStoreServiceImpl.calculateClosingStock(detail.getRawMaterial())));
			builder.sysReqQty(dailyConsumption.multiply(BigDecimal.valueOf(totalDays)));
			builder.sysUnit(detail.getRawMaterial().getNameEnglish());
			builder.sysUnitHierarchy(sysQtyUnitHierarchy);
		}

		// Safely map Unit fields
		if (detail.getRequestQtyUnit() != null) {
			UnitHierarchyDto requestQtyUnitHierarchy = unitMasterService
					.getParentUnitsWithChildren(detail.getRequestQtyUnit().getId());

			builder.requestQtyUnitId(detail.getRequestQtyUnit().getId());
			builder.requestQtyUnitName(detail.getRequestQtyUnit().getNameEnglish());
			builder.requestUnitHierarchy(requestQtyUnitHierarchy);
		}

		if (detail.getApprovedQtyUnit() != null) {
			UnitHierarchyDto approvedQtyUnitHierarchy = unitMasterService
					.getParentUnitsWithChildren(detail.getApprovedQtyUnit().getId());

			builder.approvedQtyUnitId(detail.getApprovedQtyUnit().getId());
			builder.approvedQtyUnitName(detail.getApprovedQtyUnit().getNameEnglish());
			builder.approvedUnitHierarchy(approvedQtyUnitHierarchy);
		}

		return builder.build();
	}
	
	@Override
	public Page<PurchaseRequestResponseDto> getAllApprovedRequest(Long userId, int page, int size) {
		if (page < 0) {
			page = 0;
		}

		if (size <= 0) {
			size = 10;
		}

		Pageable pageable = PageRequest.of(page, size);

		Page<PurchaseRequestEntity> purchaseRequests = purchaseRequestRepository
				.findByUserIdAndIsDeleteFalseAndStatusIgnoreCase(userId, "APPROVED", pageable);

		return purchaseRequests.map(request -> PurchaseRequestResponseDto.builder().id(request.getId())
				.requestCode(request.getRequestCode())
				.startDate(request.getStartDate() != null ? DateUtils.formatLocalDate(request.getStartDate()) : null)
				.endDate(request.getEndDate() != null ? DateUtils.formatLocalDate(request.getEndDate()) : null)
				.status(request.getStatus()).remarks(request.getRemarks()).userId(request.getUserId())
				.approvedBy(request.getApprovedBy()).build());
	}

	private long calculateDaysBetween(String startDateStr, String endDateStr) {
		if (startDateStr == null || startDateStr.trim().isEmpty() || endDateStr == null
				|| endDateStr.trim().isEmpty()) {
			return 0L;
		}

		try {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			LocalDate startDate = LocalDate.parse(startDateStr.trim(), dateFormatter);
			LocalDate endDate = LocalDate.parse(endDateStr.trim(), dateFormatter);

			if (startDate.isAfter(endDate)) {
				return 0L;
			}

			return ChronoUnit.DAYS.between(startDate, endDate) + 1;
		} catch (Exception e) {
			return 0L;
		}
	}

	private boolean isNewRecord(Long id) {
		return id == null || id <= 0;
	}
	
	private BigDecimal parseBigDecimal(String value) {
	    if (value == null || value.trim().isEmpty()) {
	        return BigDecimal.ZERO;
	    }

	    try {
	        return new BigDecimal(value.trim());
	    } catch (NumberFormatException e) {
	        return BigDecimal.ZERO;
	    }
	}
	
	@Override
	public String generatePurchaseApprovalSheetReport(Long purchaseApprovalRequestId) {

		return null;
	}
}
