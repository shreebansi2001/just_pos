package com.crmportal.request.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.crmportal.enums.EntryType;
import com.crmportal.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventQuotationSecurityDepositRequestDto {

	@NotNull(message = "Id is required.")
	private Long id;
	
	@NotNull(message = "Event id is required.")
	private Long eventId;
	
	@NotNull(message = "Quotation id is required.")
	private Long quotationId;
	
	@NotNull(message = "User id is required.")
	private Long userId;
	
	private Long cashAccountId;
	
	private Long bankAccountId;
	
	private PaymentMode paymentMode;
	
	private String description;
	
	@NotNull(message = "Amount is required.")
	private BigDecimal amount;

	@NotNull(message = "Payment date & time is required.")
	private String paymentDateTime;	

	@NotNull(message = "Entry type is required.")
	private EntryType entryType;
}
