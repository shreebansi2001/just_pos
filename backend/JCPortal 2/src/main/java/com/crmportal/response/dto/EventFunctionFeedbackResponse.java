package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionFeedbackResponse {

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

	private String eventName;

	private Long eventFunctionId;

	private String functionName;

	private Long userId;

	private Long memberId;

	private String memberName;
}