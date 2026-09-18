package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgencyWiseItemResponseDto {

	private String itemNameEnglish;
	private String itemNameHindi;
	private String itemNameGujarati;
	private Integer pax;
}
