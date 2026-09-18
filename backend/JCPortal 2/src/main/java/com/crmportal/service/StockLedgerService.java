package com.crmportal.service;

import com.crmportal.response.dto.StockLedgerResponseDto;

public interface StockLedgerService {
    StockLedgerResponseDto getStockLedger(Long rawMaterialId, String fromDate, String toDate, Long userId);
    
    byte[] generatePdfReport(Long rawMaterialId, String fromDate, String toDate, Long userId, Integer isCompanyDetails);
}