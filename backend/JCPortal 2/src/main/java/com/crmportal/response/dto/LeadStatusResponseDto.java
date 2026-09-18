package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadStatusResponseDto {

	private Long leadStatusId;
	
	private String statusName;
	
	private String colorCode;
	
	private Boolean isDeleted;
	
	private Boolean isActive;
	
	private String createdAt;
	
	private String updatedAt;
	
	private Long leadStatusTypeId;
	
	private Long userId;
}
