package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgencyResponseDto {

	private Long contactId;
	private String contactName;
	private String contactNameHindi;
	private String contactNameGujarati;
	
	private List<FunctionDetailsResponseDto> functionDetails;
}
