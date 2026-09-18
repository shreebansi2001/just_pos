package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowUpResponseDto {
	
	private Long id;
	private String followUpType;
	private String followUpStatus;
	private String followUpDate; 
	private String clientRemarks;
	private String employeeRemarks;
	private Boolean isDelete;
	private String createdAt;
}
