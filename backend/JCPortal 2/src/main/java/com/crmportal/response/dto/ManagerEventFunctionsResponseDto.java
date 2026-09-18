package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerEventFunctionsResponseDto {

	private Long eventFunctionId;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private String functionDate;
	private String startTime;
	private String endTime;
	private Integer pax;
	private String functionVenue;

}
