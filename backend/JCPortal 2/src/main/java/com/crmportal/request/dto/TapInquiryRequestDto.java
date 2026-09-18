package com.crmportal.request.dto;

import lombok.Data;

@Data
public class TapInquiryRequestDto {

	private String companyName;

	private String countryCode;

	private String contactNo;

	private String functionName;

	private String venueAddress;

	private String eventDate;

	private String eventTime;

	private Integer totalTab;

	private String timeSlot;

	private Double estimatedBudget;

	private Long cityId;

	private Long stateId;

}
