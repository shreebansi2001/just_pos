package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadPipelineResponseDto {

	private Long pipelineId;
	
	private Long stageId;
	
	private String stageType;
	
	private String stage;
	
	private Long leadId;
	
	private String clientName;
	
	private String clientContactNo;
	
	private BigDecimal estimateAmount;
	
	private String leadFollowUpDate;
	
	private String city;
	
	private Long leadAssignId;
	
	private String leadAssignName;
	
	private String leadCode;
	
	private String leadCreatedAt;

	private String leadUpdatedAt;
	
	private String planName;
	
	private String closeDate;
	
	private String description;
	
	private String leadSource;
	
}
