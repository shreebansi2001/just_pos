package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventWiseGeneralFixResponseDto {

	private Long eventId;
	private String eventName;
	private Integer pax;
	
	private List<EventWiseGeneralFixCategoryResponseDto> details;
}
