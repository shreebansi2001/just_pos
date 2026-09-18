package com.crmportal.service;

import java.util.List;
import com.crmportal.request.dto.StoreIssueReturnRequestDto;
import com.crmportal.response.dto.PocodeWithStatusResponseDto;
import com.crmportal.response.dto.PurchaseOrderStoreResponseDto;
import com.crmportal.response.dto.StoreIssueReturnResponseDto;

public interface StoreIssueReturnService {

    StoreIssueReturnResponseDto addOrUpdate(StoreIssueReturnRequestDto request);

    // Auto-fill: user types pocode → get party, invoicetype, items
    PurchaseOrderStoreResponseDto getStoreIssueDetailsByPocode(String pocode, Long userId);

    // All pocodes for dropdown
    List<PocodeWithStatusResponseDto> getAllStorePocodes(Long userId);

    List<StoreIssueReturnResponseDto> getByUser(Long userId);

    StoreIssueReturnResponseDto getBySirId(Long sirId);

    // All returns against one store issue
    List<StoreIssueReturnResponseDto> getByStoreIssueId(Long storeIssueId, Long userId);

    void deleteBySirId(Long sirId);
    
    byte[] generatePdfReport(Long sirId, Long userId, Integer isCompanyDetails);
    
    byte[] generateExcelReport(Long sirId, Long userId);
    
    byte[] generatePdfReportByStorePoId(Long storePoId, Long userId, Integer isCompanyDetails);
}