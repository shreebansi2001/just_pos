package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloseDateResponseDto {

	private Long closeDateId;
	
	private String startDate;
	
	private String closeDate;
	
	private Integer year;
	
	private Integer month; 
	
	private Boolean isActive;
}
