package com.crmportal.request.dto;

import java.time.LocalDateTime;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionRevisionHistoryRequestDto {

	private Long id;
	private String revisionDate;
	private Long approvedBy;
	private String changes;
	private Long eventId;
	private Long userId;
}
