package com.crmportal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.crmportal.request.dto.PdfWithPriceRequestDto;
import com.crmportal.request.dto.PurchaseOrderStoreRequestDto;
import com.crmportal.response.dto.ChefRequisitionResponseDto;
import com.crmportal.response.dto.CrcodeResponseDto;
import com.crmportal.response.dto.EventPartiesResponseDto;
import com.crmportal.response.dto.PriceItemResponseDto;
import com.crmportal.response.dto.PurchaseOrderResponseDto;
import com.crmportal.response.dto.PurchaseOrderStoreResponseDto;


public interface PurchaseOrderStoreService {

    PurchaseOrderStoreResponseDto addOrUpdate(PurchaseOrderStoreRequestDto request);

    List<PurchaseOrderStoreResponseDto> getByUser(Long userId);

    PurchaseOrderStoreResponseDto getByPoId(Long poId);
    
    void deleteByPoId(Long poId);
    
    byte[] generatePdfReport(Long poId, Long userId, Integer isCompanyDetails);
    
    List<CrcodeResponseDto> getAllCrcodes(Long userId);
    ChefRequisitionResponseDto getByCrcode(String crcode, Long userId);
    
    PurchaseOrderStoreResponseDto updateStatus(Long poId, String status);
    
    byte[] generateExcelReport(Long poId, Long userId);
    
    List<PriceItemResponseDto> getPricesForPo(Long poId, Long userId);

    byte[] generatePdfReportWithPrice(PdfWithPriceRequestDto request, Long userId);

	List<EventPartiesResponseDto> getAllPartiesWithEvent(Long userId);
	
	 PurchaseOrderResponseDto getByPocode(String crcode, Long userId);

	String generateDatewiseStoreIssueReport(String startDate, String endDate, Long userId, Integer isCompanyDetails,
			Integer isWithPrice, String priceType, HttpServletRequest re, Long kitchenTypeId);
}