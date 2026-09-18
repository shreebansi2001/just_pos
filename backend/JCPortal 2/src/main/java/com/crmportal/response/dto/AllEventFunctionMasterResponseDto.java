package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllEventFunctionMasterResponseDto {

	private Long id;

	private String functionName;

	private String functionNameHindi;

	private String functionNameGujarati;

	private String functionStartDateTime;

	private String functionEndDateTime;

	private Long eventId;

	private String eventName;

	private String eventNameHindi;

	private String eventNameGujarati;

	private String eventStartDateTime;

	private String eventEndDateTime;

	private Long partyId;

	private String partyName;

	private String partyNameHindi;

	private String partyNameGujarati;
	
	private String eventNo;

}
