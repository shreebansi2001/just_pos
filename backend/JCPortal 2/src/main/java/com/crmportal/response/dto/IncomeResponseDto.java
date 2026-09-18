package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.crmportal.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeResponseDto {

//	private BigDecimal totalRevenue;
//	private Long totalInvoiceCreated;
//	private Long totalPendingInvoice;
//	private BigDecimal totalAmount;
//	private BigDecimal totalPendingAmount;
//	private Long totalActiveUsers;
//	private Long totalNoInvoiceCreated;
	
	// all
	private BigDecimal totalRevenue;
	private BigDecimal totalAmount;
	private Long totalActiveUsers;
	private Long totalNoInvoiceCreated;

	// invoice
	private Long totalInvoiceCreatedThisMonth;
	private Long totalInvoiceCreatedPrevAllMonth;
	private BigDecimal totalInvoiceAmountThisMonth;
	private BigDecimal totalInvoiceAmountPrevAllMonth;
	private BigDecimal totalInvoicePaidAmountThisMonth;
	private BigDecimal totalInvoicePaidAmountPrevAllMonth;
	private BigDecimal totalInvoiceUnpaidAmountThisMonth;
	private BigDecimal totalInvoiceUnpaidAmountPrevAllMonth;

	// account entry
	private Long totalAccountEntry;
	private Long totalCashEntry;
	private Long totalBankEntry;
	private BigDecimal totalCashAmount;
	private BigDecimal totalBankAmount;
	private BigDecimal totalAccountEntryAmount;
	
	private Map<PaymentMode, BigDecimal> paymentModeAmount; 	
	private List<IncomeListResponseDto> payments;
}
