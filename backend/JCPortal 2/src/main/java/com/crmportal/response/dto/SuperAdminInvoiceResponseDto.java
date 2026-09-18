package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;

import com.crmportal.enums.TaxType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuperAdminInvoiceResponseDto {

	private Long invoiceId;
	
	private Long customerId;
	
	private String customerName;
	
	private String billingAddress;
	
	private String shippingAddress;
	
	private String billingName;
	
	private String gstNumber;
	
	private String invoiceCode;
	
	private String invoiceDate;
	
	private String terms;
	
	private String dueDate;
	
	private Long salesPersonId;
	
	private String salesPersonName;
	
	private String customerNotes;
	
	private BigDecimal subTotal;
	
	private BigDecimal discountPer;
	
	private BigDecimal discountAmount;
	
	private TaxType taxType;
	
	private BigDecimal gstPercent;
	
	private BigDecimal gstAmount;
	
	private BigDecimal adjust_amount;
	
	private BigDecimal totalAmount;
	
	private String tnc;
	
	private String docPath;
	
	private String createdAt;
	
	private String updatedAt;
	
	private Boolean isDelete;
	
	private List<SuperAdminInvoiceItemsResponseDto> items;
	
	private String status;
	
	private List<InvoicePaymentHistoryResponseDto> payments;
	
	private BigDecimal totalPaidAmount = BigDecimal.ZERO;
	
	private BigDecimal totalUnpaidAmount = BigDecimal.ZERO;
	
}
