package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDashboardDto {

	private Long totalInvoiceCount;
	
	private BigDecimal totalInvoiceAmount;

	private Long totalPendingInvoiceCount;
	
	private BigDecimal totalPendingInvoiceAmount;

	private Long totalPaidInvoiceCount;
	
	private BigDecimal totalPaidInvoiceAmount;

	private BigDecimal totalPendingAmount;

	private List<MonthWiseDataDto> months;
}
