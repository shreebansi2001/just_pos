package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkingReportResponseDto {

	private String inquiryDate;
	
	private String eventNo;
	
	private String partyName;
	
	private String mobileNo;
	
	private String partyAddress;
	
	private String eventVenue;
	
	private String eventStartDate;
	
	private String eventEndDateTime;
	
	private String eventStartTime;
	
	private String eventName;
}
