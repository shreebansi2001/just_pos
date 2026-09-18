package com.crmportal.request.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadStatusRequestDto {

	@NotNull(message = "Lead status id is required.")
	private Long leadStatusId;
	
	@NotNull(message = "Lead status name is required.")
	private String statusName;
	
	private String colorCode;
	
	private Boolean isActive;
	
	private Long lead_status_type_id;
	
	private Long userId;
}
