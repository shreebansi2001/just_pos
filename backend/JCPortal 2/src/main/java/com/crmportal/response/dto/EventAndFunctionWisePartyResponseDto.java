package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventAndFunctionWisePartyResponseDto {

	private Long id;

	private String nameEnglish;

	private String nameHindi;

	private String nameGujarati;
	
	private String contactNo;
}
