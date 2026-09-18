package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummaryResponseDto {

	private String eventDate;
	private String eventName;
	private String functionName;
	private String functionPax;
	private String functionVenue;
	private String session;
	private String partyName;
	private String managerName;
	private String status;
	private String eventNo;
	private String mobileNo;
	private String inquiryDate;
	private Long eventId;
	private String lastUpdatedBy;
	private Integer subTotal;
	private Integer transportation;
	private Integer grandTotal;
}
