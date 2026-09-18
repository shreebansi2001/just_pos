package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventAdvancePaymentResponseDto {

	private Long id;
	
	private String paymentDate;
	
	private BigDecimal amount;
	
	private String paymentMode;
	
	private Long entryBy;
	
	private String entryByName;
	
	private String remark;
	
	private String referenceId;
	
	private Long userId;
	
	private Long eventId;
	
	private Long cashId;
	
	private Long bankId;
	
	private Long eventFunctionId;
	
	private Long banquetHallId;
	
	private String createdAt;
	
	private String updatedAt;
	
	private Boolean isDelete;
	
}
