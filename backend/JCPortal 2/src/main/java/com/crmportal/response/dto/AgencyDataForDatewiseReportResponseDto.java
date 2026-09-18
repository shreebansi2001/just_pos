package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgencyDataForDatewiseReportResponseDto {

	private Long partyId;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private String contactNo;
	private List<FnDetailsForDatewiseReportResponseDto> functions;
}
