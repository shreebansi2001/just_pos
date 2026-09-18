package com.crmportal.request.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFoodTestingRequestDto {

	private Long id;
	
	private Long eventId;
	
	private Long eventFunctionId;
	
	private Long testerId;
	
	private Long userId;
	
	private List<EventFoodTestingMenuRequestDto> selectedCat;
}
