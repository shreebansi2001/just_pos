package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadDistributionResponseDto {

	private Long leadAssignId;
	private String assignName;
	private Integer completedLeads;
	private Integer pendingLeads;
	private Integer totalLeads;
}
