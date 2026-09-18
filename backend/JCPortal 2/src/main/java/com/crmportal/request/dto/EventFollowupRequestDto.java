package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFollowupRequestDto {

	private Long id;
	private String followupDate;
	private String description;
	private Long userId;
	private Long eventId;
	private Long managerId;
	private Boolean isDone;

}
