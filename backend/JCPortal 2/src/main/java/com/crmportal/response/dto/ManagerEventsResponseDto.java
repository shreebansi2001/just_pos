package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerEventsResponseDto {

	private Long eventId;
	private String eventNo;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private String partyNameEnglish;
	private String partyNameHindi;
	private String partyNameGujarati;
	private String partyMobileNo;
	private String eventDate;
	private String venueNameEnglish;
	private String venueNameHindi;
	private String venueNameGujarati;
	private String status;
	private List<ManagerEventFunctionsResponseDto> eventFunctions;
}
