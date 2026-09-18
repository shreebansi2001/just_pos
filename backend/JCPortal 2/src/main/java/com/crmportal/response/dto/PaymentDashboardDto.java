package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDashboardDto {

	private BigDecimal totalAmount;
    
	private BigDecimal paidAmount;
    
	private BigDecimal unpaidAmount;

    private List<MonthWiseDataDto> months;
}
