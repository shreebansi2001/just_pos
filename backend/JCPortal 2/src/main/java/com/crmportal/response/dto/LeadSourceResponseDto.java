package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadSourceResponseDto {

	private Long leadSourceId;
	
	private String sourceName;
	
	private Boolean isDeleted;
	
	private String createdAt;
	
	private String updatedAt;
	
	private Long userId;
}
