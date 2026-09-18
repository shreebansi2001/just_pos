package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionWithManagersResponseDto {

	private Long eventId;
	private String eventNameEnglish;
	private String eventNameHindi;
	private String eventNameGujarati;
	private String partyNameEnglish;
	private String partyNameHindi;
	private String partyNameGujarati;
	private String eventStartDate;
	private String eventEndDate;
	private String venueEnglish;
	private String venueHindi;
	private String venueGujarati;
	private String status;
	private List<EventFunctionManagerListResponseDto> eventFunctions;
}
