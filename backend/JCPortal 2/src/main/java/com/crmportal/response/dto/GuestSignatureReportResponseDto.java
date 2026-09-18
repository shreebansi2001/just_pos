package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestSignatureReportResponseDto {

	private Long guestSignatureId;
	
	private String particulars;
	
	private Integer persons;
	
	private Integer extra;
	
	private Long eventFunctionId;
	
	private String functionName;
}
