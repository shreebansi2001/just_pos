package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventAdvancePaymentReportResponseDto {

	private Long eventId;

	private String eventNo;
	
	private String eventName;
	
	private String eventDate;
	
	private String partyName;
	
	private String cmpName;
	
	private String cmpEmail;
	
	private String cmpAddress;
	
	private String logo;
	
	private String cmpContactNo;
	
	private String eventStartTimeStamp;
	
	private Long advancePaymentId;
	
	private String paymentDate;
	
	private BigDecimal amount;
	
	private String paymentMode;
	
	private Long entryBy;
	
	private String entryByName;
	
	private String remark;
	
	private String referenceId;
	
	private Long userId;
	
	private Long cashId;

	private String cashName;
	
	private Long bankId;
	
	private String bankName;
	
	private String venue;
	
	private String shift;
	
}
