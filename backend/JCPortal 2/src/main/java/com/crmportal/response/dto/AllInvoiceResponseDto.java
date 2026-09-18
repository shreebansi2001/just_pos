package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllInvoiceResponseDto {

	private Long totalInvoicesThisMonth;
	
	private BigDecimal totalAmountThisMonth;
	
	private BigDecimal totalPaidAmountThisMonth;
	
	private BigDecimal totalUnPaidAmountThisMonth;
	
//	private Long totalPaidInvoiceCountThisMonth;
	
//	private Long totalUnpaidInvoiceCountThisMonth;
	
//	private Long totalPendingInvoiceCountThisMonth;
	
	private Long totalUnpaidInvoiceCountPrevAllMonth;
	
	private BigDecimal totalUnpaidInvoiceAmountPrevAllMonth;
	
	private BigDecimal totalPaidAmountPrevAllMonth;
	
	private BigDecimal totalRemainingAmountPrevAllMonth;
	
	private BigDecimal totalInvoiceAmount;
	
//	private Long totalUnpaidInvoiceCount;
	
	private Long totalInvoiceCount;
	
	private BigDecimal totalPaidAmount;
	
	private BigDecimal totalUnpaidAmount;
	
	private List<SuperAdminInvoiceResponseDto> invoices;
	
	private List<SuperAdminInvoiceResponseDto> totalPaidInvoiceThisMonth;
}
