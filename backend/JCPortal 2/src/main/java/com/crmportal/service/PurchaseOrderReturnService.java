package com.crmportal.service;

import java.util.List;
import com.crmportal.request.dto.PurchaseOrderReturnRequestDto;
import com.crmportal.response.dto.PocodeWithStatusResponseDto;
import com.crmportal.response.dto.PurchaseOrderResponseDto;
import com.crmportal.response.dto.PurchaseOrderReturnResponseDto;

public interface PurchaseOrderReturnService {

    void addOrUpdate(PurchaseOrderReturnRequestDto dto);

    // Fetch PO details by pocode — used when user types PO number and presses Enter
    PurchaseOrderResponseDto getPoDetailsByPocode(String pocode, Long userId);

    List<PurchaseOrderReturnResponseDto> getByUser(Long userId);

    List<PurchaseOrderReturnResponseDto> getByPorId(Long porId);

    List<PurchaseOrderReturnResponseDto> getByPoId(Long poId);

    void deleteByPorId(Long porId);
    
    List<PocodeWithStatusResponseDto> getAllPocodes(Long userId);
    
    byte[] generateExcelReport(Long porId, Long userId);
    
    byte[] generatePdfReport(Long porId, Long userId, Integer isCompanyDetails, Integer isPrice);
}