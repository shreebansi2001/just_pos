package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionRevisionHistoryResponseDto {

	private Long id;
	private String revisionDate;
	private Long approvedBy;
	private String approvedByName;
	private String changes;
	private Long eventId;
	private Long userId;
}
