package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventByDateResponseDto {

	private String functionDate;
	private String eventName;
	private String sessionDateTime;
	private Integer pax;
	private String venueName;
	private String guestName;
	private String mgrName;
	private String menuReleasingStatus;
	
}
