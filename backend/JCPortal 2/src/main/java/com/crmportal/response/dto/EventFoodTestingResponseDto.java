package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFoodTestingResponseDto {

	private Long id;
	
	private Long eventId;
	
	private Long eventFunctionId;
	
	private Long testerId;
	
	private List<EventFoodTestingMenuResponseDto> selectedCat;
}
