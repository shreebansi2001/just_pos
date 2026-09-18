package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatewisePurchaseReportResponseDto {

    private Long supplierId;

    private String supplierName;
    
    private BigDecimal totalAmount;

    private List<DatewisePurchaseReportDateResponseDto> dates;
}
