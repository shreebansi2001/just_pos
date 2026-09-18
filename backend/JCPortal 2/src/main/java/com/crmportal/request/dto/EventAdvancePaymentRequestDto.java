package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventAdvancePaymentRequestDto {

	private Long id;
	
	private String paymentDate;
	
	private BigDecimal amount;
	
	private String paymentMode;
	
	private Long entryBy;
	
	private String remark;

	private String referenceId;
	
	private Long userId;
	
	private Long eventId;
	
	private Long bankId;

	private Long cashId;

	private Long eventFunctionId;
	
	private Long banquetHallId;
	
}
