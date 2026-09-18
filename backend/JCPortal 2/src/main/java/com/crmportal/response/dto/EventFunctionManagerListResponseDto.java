package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionManagerListResponseDto {

	private Long eventFunctionId;
	private String functionNameEnglish;
	private String functionNameHindi;
	private String functionNameGujarati;
	private String venue;
	private String functionStartDate;
	private String functionEndDate;
	private Integer pax;
	private List<EventFunctionManagersResponseDto> managers;
}
