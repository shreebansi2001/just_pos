package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadSubSourceResponseDto2 {

	private Long leadSubSourceId;
	
	private String name;
	
	private String dateTime;
	
	private String description;
	
	private String createdAt;
	
	private String updatedAt;
	
	private Boolean isDelete;
}
