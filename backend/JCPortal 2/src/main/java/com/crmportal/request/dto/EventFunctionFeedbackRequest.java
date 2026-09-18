package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionFeedbackRequest {

	private Long id;

	private String name;

	private String mobileno;

	private Integer providedServices;

	private Integer satisfiedServices;

	private Integer expectation;

	private Integer overallExperience;

	private String recommend;

	private String description;

	private Long eventId;

	private Long eventFunctionId;

	private Long userId;

	private Long memberId;
}