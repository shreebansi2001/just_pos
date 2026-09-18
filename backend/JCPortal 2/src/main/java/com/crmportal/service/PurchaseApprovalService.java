package com.crmportal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.crmportal.request.dto.PurchaseRequestDto;
import com.crmportal.response.dto.PurchaseRequestDetailsResponseDto;
import com.crmportal.response.dto.PurchaseRequestResponseDto;

@Service
public interface PurchaseApprovalService {

	String generatePurchaseRequestCode(Long userId);

	Boolean addUpdatePurchaseRequest(PurchaseRequestDto request);

	PurchaseRequestResponseDto getPurchaseRequestById(Long purchaseRequestId);

	List<PurchaseRequestResponseDto> getAllPurchaseRequests(Long userId, String status);

	Page<PurchaseRequestDetailsResponseDto> getDetailsByRawMaterialId(Long rawMaterialCatId, String startDate,
			String endDate, int page, int size, String rawMaterialName, Long purchaseRequestId, Boolean isAllData);

	Page<PurchaseRequestResponseDto> getAllApprovedRequest(Long userId, int page, int size);

	String generatePurchaseApprovalSheetReport(Long purchaseApprovalRequestId);

}
