package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.crmportal.enums.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesInvoiceRequestDto {
	
	private Long id;
	
	private String invoiceNo;
	
	private String paymentDate;
	
	private BigDecimal invoiceAmount;
	
	private BigDecimal dueAmount;
	
	private BigDecimal totalAmount;
	
	private String paymentMode;
	
	private String reference;
	
	private Long bankId;
	
	private Long cashId;
	
	private AccountType accountType;
	
	private Long userId;
	
	private Long eventId;
}
