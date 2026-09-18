package com.crmportal.response.dto;

import java.math.BigDecimal;

import com.crmportal.enums.EntryType;
import com.crmportal.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventQuotationSecurityDepositResponseDto {

	private Long id;
	
	private Long eventId;

	private Long quotationId;

	private Long userId;

	private Long cashAccountId;

	private Long bankAccountId;

	private PaymentMode paymentMode;

	private String description;

	private BigDecimal amount;
	
	private String paymentDateTime;
	
	private String createdAt;
	
	private String updatedAt;
	
	private EntryType entryType;
}
