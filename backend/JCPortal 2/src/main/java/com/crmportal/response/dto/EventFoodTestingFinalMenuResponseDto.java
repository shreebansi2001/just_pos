package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFoodTestingFinalMenuResponseDto {

	private Long eventId;
	
	private Long eventFunctionId;
	
	private Long testerId;
	
	private Long userId;
	
	private List<MenuPreparationSelectedItemDetailsResponseDto> menuPreparationItems;

	private List<EventFoodTestingResponseDto> selectedMenuPreparation; 
}
