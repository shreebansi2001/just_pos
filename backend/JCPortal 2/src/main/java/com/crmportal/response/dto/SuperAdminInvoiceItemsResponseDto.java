package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuperAdminInvoiceItemsResponseDto {

	private Long invoiceItemId;
	
	private Long planHistoryId;
	
	private String itemName;
	
	private BigDecimal qty;
	
	private BigDecimal rate;
	
	private BigDecimal amount;
	
	private String description;
	 
	private LocalDateTime createdAt;
	
	private String hsnCode;
	
	private BigDecimal taxPercent;
	
	private BigDecimal taxAmount;
}
