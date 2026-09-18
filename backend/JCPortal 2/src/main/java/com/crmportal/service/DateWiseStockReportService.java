package com.crmportal.service;

import com.crmportal.response.dto.DateWiseStockReportResponseDto;

public interface DateWiseStockReportService {
    DateWiseStockReportResponseDto getReport(
            Long categoryId,
            Long stockTypeId,
            String fromDate,
            String toDate,
            Long userId,
            Integer pageNo,
            Integer pageSize, String search);
    
    byte[] generatePdfReport(Long categoryId, Long stockTypeId, String fromDate,
            String toDate, Long userId, Integer isCompanyDetails, String search);

    byte[] generateExcelReport(Long categoryId, Long stockTypeId, String fromDate,
            String toDate, Long userId, String search);
}