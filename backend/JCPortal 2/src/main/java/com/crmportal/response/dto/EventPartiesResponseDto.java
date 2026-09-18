package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventPartiesResponseDto {

	private Long eventId;
	private String eventName;
	private String eventDate;
	private String eventNo;
	
	private Long partyId;
	private String partyName;
}
