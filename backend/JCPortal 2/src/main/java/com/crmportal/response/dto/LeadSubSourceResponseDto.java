package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadSubSourceResponseDto {

	private Long leadSubSourceId;
	
	private LeadSourceResponseDto leadSource;
	
	private String name;
	
	private String dateTime;
	
	private String description;
	
	private String createdAt;
	
	private String updatedAt;
	
	private Boolean isDelete;
	
	private Long userId;
}
