package com.crmportal.request.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadSubSourceRequestDto {

	@NotNull(message = "Lead source id is required.")
	private Long leadSourceId;
	
	@NotNull(message = "Lead subsource name is required.")
	private String name;
	
	@NotNull(message = "DateTime is required.")
	private String dateTime;
	
	private String description;
	
	private Long userId;
}
