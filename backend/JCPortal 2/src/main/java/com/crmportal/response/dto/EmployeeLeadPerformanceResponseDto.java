package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeLeadPerformanceResponseDto {
	private Long leadAssignId;
	private String userName;
	private Integer hotLeads;
	private Integer coldLeads;
	private Integer wonLeads;
	private Integer lostLeads;
	private Integer clientDemoLeads;
	private Integer onTimeDelivery;
	private Double qualityScore;
	private Integer totalLeads;
}
