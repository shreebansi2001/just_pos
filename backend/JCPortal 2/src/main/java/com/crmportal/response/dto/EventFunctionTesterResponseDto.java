package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionTesterResponseDto {

	private Long testerId;
	
	private String testerName;
	
	private Long eventId;
	
	private Long eventFunctionId;
	
	private Integer members;
	
	public EventFunctionTesterResponseDto(Long testerId, String testerName, Long eventId, Long eventFunctionId,
			Long members) {
		this.testerId = testerId;
		this.testerName = testerName;
		this.eventId = eventId;
		this.eventFunctionId = eventFunctionId;
		this.members = members != null ? members.intValue() : 0;
	}
}
