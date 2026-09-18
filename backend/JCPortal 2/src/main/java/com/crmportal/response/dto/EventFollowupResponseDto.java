package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFollowupResponseDto {
	private Long id;
	private Long eventId;
	private String eventName;
	private String followupDate;
	private String description;
	private Long userId;
	private Long managerId;
	private String managerName;
	private String createdAt;
	private Boolean isDone;
}
