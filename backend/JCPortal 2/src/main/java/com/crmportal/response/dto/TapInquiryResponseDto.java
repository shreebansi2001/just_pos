package com.crmportal.response.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TapInquiryResponseDto {

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
	private String cityName;

	private Long stateId;
	private String stateName;

}