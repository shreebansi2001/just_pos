package com.crmportal.service;


import java.util.List;

import com.crmportal.request.dto.PurchaseOrderRequestDto;
import com.crmportal.response.dto.PurchaseOrderRawMaterialDetailsResponseDto;
import com.crmportal.response.dto.PurchaseOrderResponseDto;


public interface PurchaseOrderService {

    void addOrUpdate(PurchaseOrderRequestDto dto, int potype);

    List<PurchaseOrderResponseDto> getByUser(Long userId);
    
    List<PurchaseOrderResponseDto> getByUserAndPotype(Long userId, int potype);

    List<PurchaseOrderResponseDto> getByPoId(Long poId);

    List<PurchaseOrderResponseDto> getByPoType(int potype);
    
    void deleteByPoId(Long poId);
    
    byte[] generatePdfReport(Long poId, Long userId, Integer isCompanyDetails, Integer isPrice);
    
    byte[] generateExcelReport(Long poId, Long userId);

	PurchaseOrderRawMaterialDetailsResponseDto getRawMaterialPrice(Long supplierId, Long rawMaterialId, Long userId);

	String generatePoCode(Long userId, String codeType);
}